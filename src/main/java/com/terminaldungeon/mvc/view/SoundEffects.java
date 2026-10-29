package com.terminaldungeon.mvc.view;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Timer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Sound effects for the Swing view, generated in code so the project needs no audio files.
// If the computer has no sound device the game simply stays silent.
final class SoundEffects {
    enum Sound { SWING, HIT, HEAL, ENEMY_DOWN, VICTORY, DEFEAT, CLICK, ERROR }

    private static final float RATE = 22050f;
    private static final AudioFormat FORMAT = new AudioFormat(RATE, 16, 1, true, false);

    private final Map<Sound, byte[]> sounds = new EnumMap<>(Sound.class);
    private final ExecutorService player = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "sound-effects");
        thread.setDaemon(true);
        return thread;
    });
    private final Random random = new Random(7);
    private boolean enabled = true;
    private volatile boolean available = true;

    SoundEffects() {
        sounds.put(Sound.SWING, toBytes(noise(0.16, 0.06, 0.55), 0.45));
        sounds.put(Sound.HIT, toBytes(mix(sweep(150, 45, 0.2, false), noise(0.05, 0.5, 0.5)), 0.85));
        sounds.put(Sound.HEAL, toBytes(concat(note(1047, 0.08), note(1319, 0.08), note(1568, 0.18)), 0.3));
        sounds.put(Sound.ENEMY_DOWN, toBytes(sweep(440, 110, 0.38, true), 0.22));
        sounds.put(Sound.VICTORY, toBytes(concat(square(523, 0.12), square(659, 0.12), square(784, 0.12),
                square(1047, 0.5)), 0.2));
        sounds.put(Sound.DEFEAT, toBytes(concat(square(392, 0.25), square(349, 0.25), square(311, 0.25),
                square(262, 0.7)), 0.2));
        sounds.put(Sound.CLICK, toBytes(note(880, 0.05), 0.25));
        sounds.put(Sound.ERROR, toBytes(concat(square(160, 0.09), silence(0.04), square(130, 0.13)), 0.2));
    }

    boolean isEnabled() {
        return enabled;
    }

    void toggle() {
        enabled = !enabled;
    }

    // plays after a delay so the sound lines up with its animation
    void play(Sound sound, long delayMs) {
        if (!enabled || !available) {
            return;
        }
        if (delayMs <= 0) {
            playNow(sound);
            return;
        }
        Timer timer = new Timer((int) delayMs, event -> playNow(sound));
        timer.setRepeats(false);
        timer.start();
    }

    private void playNow(Sound sound) {
        byte[] data = sounds.get(sound);
        player.execute(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(FORMAT, data, 0, data.length);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.start();
            } catch (LineUnavailableException | IllegalArgumentException | SecurityException e) {
                available = false;
            }
        });
    }

    // ---------- sound generation ----------

    private static float[] note(double hz, double seconds) {
        return sweep(hz, hz, seconds, false);
    }

    private static float[] square(double hz, double seconds) {
        return sweep(hz, hz, seconds, true);
    }

    // a tone that slides from one pitch to another and fades out
    private static float[] sweep(double fromHz, double toHz, double seconds, boolean square) {
        int length = (int) (RATE * seconds);
        float[] samples = new float[length];
        double phase = 0;
        for (int i = 0; i < length; i++) {
            double progress = (double) i / length;
            phase += 2 * Math.PI * (fromHz + (toHz - fromHz) * progress) / RATE;
            double wave = square ? Math.signum(Math.sin(phase)) * 0.6 : Math.sin(phase);
            samples[i] = (float) (wave * envelope(i, length));
        }
        return samples;
    }

    // filtered noise; the filter opens up over time, which sounds like a swoosh
    private float[] noise(double seconds, double filterStart, double filterEnd) {
        int length = (int) (RATE * seconds);
        float[] samples = new float[length];
        double filtered = 0;
        for (int i = 0; i < length; i++) {
            double progress = (double) i / length;
            double filter = filterStart + (filterEnd - filterStart) * progress;
            filtered += filter * ((random.nextDouble() * 2 - 1) - filtered);
            samples[i] = (float) (filtered * 1.8 * envelope(i, length));
        }
        return samples;
    }

    private static float[] silence(double seconds) {
        return new float[(int) (RATE * seconds)];
    }

    // quick fade-in, then a smooth fade-out
    private static double envelope(int index, int length) {
        double attack = Math.min(1.0, index / (RATE * 0.005));
        return attack * Math.pow(1.0 - (double) index / length, 1.5);
    }

    private static float[] concat(float[]... parts) {
        int total = 0;
        for (float[] part : parts) {
            total += part.length;
        }
        float[] samples = new float[total];
        int position = 0;
        for (float[] part : parts) {
            System.arraycopy(part, 0, samples, position, part.length);
            position += part.length;
        }
        return samples;
    }

    private static float[] mix(float[] first, float[] second) {
        float[] samples = new float[Math.max(first.length, second.length)];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (i < first.length ? first[i] : 0) + (i < second.length ? second[i] : 0);
        }
        return samples;
    }

    private static byte[] toBytes(float[] samples, double volume) {
        byte[] data = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            double value = Math.max(-1.0, Math.min(1.0, samples[i] * volume));
            short sample = (short) (value * Short.MAX_VALUE);
            data[i * 2] = (byte) sample;
            data[i * 2 + 1] = (byte) (sample >> 8);
        }
        return data;
    }
}
