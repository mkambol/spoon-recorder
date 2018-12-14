package org.hitachivantara.spoonrecorder.adapters;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.hitachivantara.spoonrecorder.SWTRecorder;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordSelectionAdapter extends SelectionAdapter {
  AtomicBoolean running = new AtomicBoolean( false );
  SWTRecorder rec;
  Shell recorderShell;
  Table eventTable;

  public RecordSelectionAdapter( Shell recorderShell, Table eventTable ){
    this.recorderShell = recorderShell;
    this.eventTable = eventTable;
  }

  @Override
  public void widgetSelected( SelectionEvent event ) {
    super.widgetSelected( event );
    if ( running.compareAndSet( true, false ) ) {
      ((Button)event.widget).setText( "Resume" );
      System.out.println( "Stopping Recording" );
      try {
        rec.close();
      } catch ( IOException e ) {
        e.printStackTrace();
      }
    } else if ( running.compareAndSet( false, true ) ) {
      System.out.println( "Start Recording" );
      ((Button)event.widget).setText( "Stop" );
      rec = new SWTRecorder( recorderShell, Paths.get( "/tmp/abcd.swt" ), eventTable );
      rec.open();
    }
  }
}
