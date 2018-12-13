package org.hitachivantara.spoonrecorder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.swt.widgets.Event;

import java.io.IOException;

import static org.hitachivantara.spoonrecorder.EventSerializer.SerializableEvent.from;

public class EventSerializer {

  static String serialize( Event event ) {

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString( from( event ) );
    } catch ( JsonProcessingException e ) {
      throw new IllegalStateException( e );
    }
  }

  static Event deserialize( String ser ) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue( ser, SerializableEvent.class ).to();
    } catch ( IOException e ) {
      throw new IllegalStateException( e );
    }
  }

  public static class SerializableEvent {
    public int x, y, type, button;

    static SerializableEvent from( Event event ) {
      SerializableEvent serializableEvent = new SerializableEvent();
      serializableEvent.x = event.x;
      serializableEvent.y = event.y;
      serializableEvent.type = event.type;
      serializableEvent.button = event.button;
      return serializableEvent;
    }

    Event to() {
      Event event = new Event();
      event.x = x;
      event.y = y;
      event.type = type;
      event.button = button;
      return event;
    }

  }


}
