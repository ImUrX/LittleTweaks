package io.github.imurx.audioswitcher.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.sound.SoundSystem;

public interface SoundSystemCallback {

    Event<SoundSystemCallback> STARTED_SYSTEM = EventFactory.createArrayBacked(SoundSystemCallback.class,
            (listeners) -> (soundSystem) -> {
                for(SoundSystemCallback listener : listeners) {
                    listener.onStateChange(soundSystem);
                }
            });

    Event<SoundSystemCallback> STOPPING_SYSTEM = EventFactory.createArrayBacked(SoundSystemCallback.class,
            (listeners) -> (soundSystem) -> {
                for(SoundSystemCallback listener : listeners) {
                    listener.onStateChange(soundSystem);
                }
            });

    void onStateChange(SoundSystem soundSystem);
}
