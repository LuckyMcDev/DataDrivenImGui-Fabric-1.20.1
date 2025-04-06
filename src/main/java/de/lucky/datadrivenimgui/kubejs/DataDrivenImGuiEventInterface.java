package de.lucky.datadrivenimgui.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface DataDrivenImGuiEventInterface {
    EventGroup GROUP = EventGroup.of("DataDrivenImGuiJsEvents");

    EventHandler EVENT_NAME = DataDrivenImGuiEventInterface.GROUP.startup("draw", () ->  DataDrivenImGuiEvent.class);
}
