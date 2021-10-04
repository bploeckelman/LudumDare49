package lando.systems.ld49;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld49.world.World;

public class Audio implements Disposable {

    public static final float MUSIC_VOLUME = 0.5f;
    public static final float SOUND_VOLUME = 0.5f;

    public static boolean soundEnabled = true;
    public static boolean musicEnabled = true;

    // none should not have a sound
    public enum Sounds {
        none,
        example,
        slingshotReload,
        slingshotPull,
        slingshotRelease,
        rodHit,
        wallHit,
        pistonUp,
        pistonDown,
        steamHiss,
        fire,
        alarm,
        downHit,
        click,
        scream

    }

    public enum Musics {
        none,
        example,
        music1,
        introMusic
    }

    public ObjectMap<Sounds, SoundContainer> sounds = new ObjectMap<>();
    public ObjectMap<Musics, Music> musics = new ObjectMap<>();

    public Music currentMusic;
    public MutableFloat musicVolume;
    public Musics eCurrentMusic;
    public Music oldCurrentMusic;

    private final Assets assets;
    private final TweenManager tween;

    public Audio(Main game) {
        this.assets = game.assets;
        this.tween = game.tween;


        putSound(Sounds.example, assets.exampleSound);
        putSound(Sounds.slingshotPull, assets.slingshotPull1);
        putSound(Sounds.slingshotPull, assets.slingshotPull2);
        putSound(Sounds.slingshotPull, assets.slingshotPull3);
        putSound(Sounds.slingshotPull, assets.slingshotPull4);
        putSound(Sounds.slingshotRelease, assets.slingshotRelease1);
        putSound(Sounds.slingshotRelease, assets.slingshotRelease2);
        putSound(Sounds.slingshotRelease, assets.slingshotRelease3);
        putSound(Sounds.slingshotRelease, assets.slingshotRelease4);
        putSound(Sounds.rodHit, assets.rodHit1);
        putSound(Sounds.rodHit, assets.rodHit2);
        putSound(Sounds.rodHit, assets.rodHit3);
        putSound(Sounds.rodHit, assets.rodHit4);
        putSound(Sounds.rodHit, assets.rodHit5);
        putSound(Sounds.rodHit, assets.rodHit6);
        putSound(Sounds.rodHit, assets.rodHit7);
        putSound(Sounds.wallHit, assets.wallHit1);
        putSound(Sounds.wallHit, assets.wallHit2);
        putSound(Sounds.wallHit, assets.wallHit3);
        putSound(Sounds.wallHit, assets.wallHit4);
        putSound(Sounds.wallHit, assets.wallHit5);
        putSound(Sounds.wallHit, assets.wallHit6);
        putSound(Sounds.wallHit, assets.wallHit7);
        putSound(Sounds.wallHit, assets.wallHit8);
        putSound(Sounds.wallHit, assets.wallHit9);
        putSound(Sounds.wallHit, assets.wallHit10);
        putSound(Sounds.pistonUp, assets.pistonUp1);
        putSound(Sounds.pistonUp, assets.pistonUp2);
        putSound(Sounds.pistonUp, assets.pistonUp3);
        putSound(Sounds.pistonUp, assets.pistonUp4);
        putSound(Sounds.pistonUp, assets.pistonUp5);
        putSound(Sounds.pistonDown, assets.pistonDown1);
        putSound(Sounds.pistonDown, assets.pistonDown2);
        putSound(Sounds.pistonDown, assets.pistonDown3);
        putSound(Sounds.pistonDown, assets.pistonDown4);
        putSound(Sounds.pistonDown, assets.pistonDown5);
        putSound(Sounds.steamHiss, assets.steamHiss1);
        putSound(Sounds.steamHiss, assets.steamHiss2);
        putSound(Sounds.steamHiss, assets.steamHiss3);
        putSound(Sounds.steamHiss, assets.steamHiss4);
        putSound(Sounds.steamHiss, assets.steamHiss5);
        putSound(Sounds.steamHiss, assets.steamHiss6);
        putSound(Sounds.steamHiss, assets.steamHiss7);
        putSound(Sounds.steamHiss, assets.steamHiss8);
        putSound(Sounds.steamHiss, assets.steamHiss9);
        putSound(Sounds.steamHiss, assets.steamHiss10);
        putSound(Sounds.fire, assets.fire1);
        putSound(Sounds.fire, assets.fire2);
        putSound(Sounds.fire, assets.fire3);
        putSound(Sounds.fire, assets.fire4);
        putSound(Sounds.fire, assets.fire5);
        putSound(Sounds.alarm, assets.alarm1);
        putSound(Sounds.downHit, assets.downHit1);
        putSound(Sounds.downHit, assets.downHit2);
        putSound(Sounds.click, assets.click1);
        putSound(Sounds.click, assets.click2);
        putSound(Sounds.scream, assets.scream1);
        putSound(Sounds.scream, assets.scream2);


        musics.put(Musics.example, assets.exampleMusic);
        musics.put(Musics.music1, assets.music1);
        musics.put(Musics.introMusic, assets.introMusic);

        musicVolume = new MutableFloat(0.3f);
        setMusicVolume(MUSIC_VOLUME, 2f);
    }

    public void update(float dt) {
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume.floatValue());
        }

        if (oldCurrentMusic != null) {
            oldCurrentMusic.setVolume(musicVolume.floatValue());
        }
    }

    @Override
    public void dispose() {
        Sounds[] allSounds = Sounds.values();
        for (Sounds sound : allSounds) {
            if (sounds.get(sound) != null) {
                sounds.get(sound).dispose();
            }
        }
        Musics[] allMusics = Musics.values();
        for (Musics music : allMusics) {
            if (musics.get(music) != null) {
                musics.get(music).dispose();
            }
        }
        currentMusic = null;
    }

    public void putSound(Sounds soundType, Sound sound) {
        SoundContainer soundCont = sounds.get(soundType);
        if (soundCont == null) {
            soundCont = new SoundContainer();
        }

        soundCont.addSound(sound);
        sounds.put(soundType, soundCont);
    }

    public long playSound(Sounds soundOption) {
        if (!soundEnabled || soundOption == Sounds.none) return -1;
        return playSound(soundOption, SOUND_VOLUME);
    }

    public long playSound(Sounds soundOption, float volume) {
        if (!soundEnabled || soundOption == Sounds.none) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont == null) {
            // Gdx.app.log("NoSound", "No sound found for " + soundOption.toString());
            return 0;
        }

        Sound s = soundCont.getSound();
        return (s != null) ? s.play(volume) : 0;
    }

    public void stopSound(Sounds soundOption) {
        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont != null) {
            soundCont.stopSound();
        }
    }

    public void stopAllSounds() {
        for (SoundContainer soundCont : sounds.values()) {
            if (soundCont != null) {
                soundCont.stopSound();
            }
        }
    }

    public Music playMusic(Musics musicOptions) {
        return playMusic(musicOptions, true);
    }

    public Music playMusic(Musics musicOptions, boolean playImmediately) {
        return playMusic(musicOptions, playImmediately, true);
    }

    public Music playMusic(Musics musicOptions, boolean playImmediately, boolean looping) {
        if (!musicEnabled) { return null; }

        if (playImmediately) {
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            // fade in out streams
            currentMusic = startMusic(musicOptions, looping);
        } else {
            if (currentMusic == null || !currentMusic.isPlaying()) {
                currentMusic = startMusic(musicOptions, looping);
            } else {
                currentMusic.setLooping(false);
                currentMusic.setOnCompletionListener(music -> {
                    currentMusic = startMusic(musicOptions, looping);
                });
            }
        }
        return currentMusic;
    }

    private Music startMusic(Musics musicOptions, boolean looping) {
        Music music = musics.get(musicOptions);
        if (music != null) {
            music.setLooping(looping);
            music.play();
        }
        return music;
    }

    public void fadeMusic(Musics musicOption) {
        if (eCurrentMusic == musicOption) return;
        Timeline.createSequence()
                .push(Tween.to(musicVolume, 1, 1).target(0).ease(Linear.INOUT))
                .push(Tween.call((type, source) -> {
                    if (currentMusic != null) currentMusic.stop();
                    eCurrentMusic = musicOption;
                    currentMusic = musics.get(musicOption);
                    currentMusic.setLooping(true);
                    currentMusic.play();
                }))
                .push(Tween.to(musicVolume, 1, 1).target(MUSIC_VOLUME).ease(Linear.INOUT))
                .start(tween);
    }

    public void stopMusic() {
        for (Music music : musics.values()) {
            if (music != null) music.stop();
        }
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void setMusicVolume(float level, float duration) {
        Tween.to(musicVolume, 1, duration).target(level).ease(Sine.IN).start(tween);
    }
}

class SoundContainer {
    public Array<Sound> sounds;
    public Sound currentSound;

    public SoundContainer() {
        sounds = new Array<Sound>();
    }

    public void addSound(Sound s) {
        if (!sounds.contains(s, false)) {
            sounds.add(s);
        }
    }

    public Sound getSound() {
        if (sounds.size > 0) {
            int randIndex = MathUtils.random(0, sounds.size - 1);
            Sound s = sounds.get(randIndex);
            currentSound = s;
            return s;
        } else {
            // Gdx.app.log("No sounds found!");
            return null;
        }
    }

    public void stopSound() {
        if (currentSound != null) {
            currentSound.stop();
        }
    }

    public void dispose() {
        if (currentSound != null) {
            currentSound.dispose();
        }
    }
}
