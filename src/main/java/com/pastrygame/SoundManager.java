package com.pastrygame;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    private static SoundManager instance;
    private Clip backgroundMusic;

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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/sounds/Fkj-Ylang Ylang (slowed + reverb).wav")
            );
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
            System.out.println("SoundManager.startBackgroundMusic: Background music started");
        } catch (Exception e) {
            System.err.println("SoundManager.startBackgroundMusic: Error playing music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
            System.out.println("SoundManager.stopBackgroundMusic: Background music stopped");
        }
    }
}

