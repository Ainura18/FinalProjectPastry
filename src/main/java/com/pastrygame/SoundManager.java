package com.pastrygame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private Clip winMusic;

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
            System.out.println("SoundManager.getInstance: Initialized SoundManager");
        }
        return instance;
    }

    public void startBackgroundMusic() {
        try {
            // Load the background music
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sounds/Fkj-Ylang Ylang (slowed + reverb).wav")
            );
            if (audioInputStream == null) {
                System.err.println("SoundManager.startBackgroundMusic: AudioInputStream is null for background music");
                return;
            }
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
            System.out.println("SoundManager.startBackgroundMusic: Background music started successfully");
        } catch (Exception e) {
            System.err.println("SoundManager.startBackgroundMusic: Error playing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isOpen()) {
            backgroundMusic.stop();
            backgroundMusic.close();
            System.out.println("SoundManager.stopBackgroundMusic: Background music stopped");
        } else {
            System.out.println("SoundManager.stopBackgroundMusic: No background music to stop or already closed");
        }
    }

    public void playWinMusic() {
        try {
            // Stop any existing win music to avoid overlap
            stopWinMusic();
            // Load the win music
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sounds/youtube_32M-4yuZVPo_audio.wav")
            );
            if (audioInputStream == null) {
                System.err.println("SoundManager.playWinMusic: AudioInputStream is null for win music");
                return;
            }
            winMusic = AudioSystem.getClip();
            winMusic.open(audioInputStream);
            winMusic.start();
            System.out.println("SoundManager.playWinMusic: Win music started successfully");
        } catch (Exception e) {
            System.err.println("SoundManager.playWinMusic: Error playing win music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopWinMusic() {
        if (winMusic != null && winMusic.isOpen()) {
            winMusic.stop();
            winMusic.close();
            System.out.println("SoundManager.stopWinMusic: Win music stopped");
        } else {
            System.out.println("SoundManager.stopWinMusic: No win music to stop or already closed");
        }
    }

    public void restartBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.setFramePosition(0); // Go to beginning
            backgroundMusic.start();
            System.out.println("SoundManager.restartBackgroundMusic: Background music restarted");
        }
    }

}
