package org.hitachivantara.spoonrecorder.handlers;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.hitachivantara.spoonrecorder.SWTRecordedEvent;
import org.hitachivantara.spoonrecorder.WidgetKey;

import java.util.function.Consumer;

public interface RecordEventHandler {

  boolean attachListener( WidgetKey key, Widget w, int eventType, Consumer<SWTRecordedEvent> recordedEventConsumer );


  default Event event( Widget w, int type, Object data ) {
    Event e = new Event();
    e.widget = w;
    e.type = type;
    e.data = data;
    return e;
  }
}
