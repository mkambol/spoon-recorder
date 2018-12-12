package org.hitachivantara.spoonrecorder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.annotations.KettleLifecyclePlugin;
import org.pentaho.di.core.lifecycle.KettleLifecycleListener;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.Spoon;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

@KettleLifecyclePlugin ( id = "SpoonRecorder", name = "Spoon UI Recorder" )
public class RecorderPlugin implements KettleLifecycleListener {

  AtomicBoolean running = new AtomicBoolean( false );
  SWTRecorder rec;

  @Override public void onEnvironmentInit() throws LifecycleException {
    PropsUI props = PropsUI.getInstance();
    Display mainDisp = Spoon.getInstance().getShell().getDisplay();
    mainDisp.asyncExec( () -> {
      Shell recorderShell = new Shell( mainDisp );
      recorderShell.setLayout( new FormLayout() );
      Button start = new Button( recorderShell, SWT.NONE );
      start.setText( "Start" );
      props.setLook( start );
      start.setLayoutData( new FormDataBuilder().top().left().result() );

      start.addSelectionListener( new SelectionAdapter() {
        @Override public void widgetSelected( SelectionEvent selectionEvent ) {
          super.widgetSelected( selectionEvent );
          if ( running.compareAndSet( true, false ) ) {
            start.setText( "Start" );
            System.out.println( "Stopping Recording" );
            try {
              rec.close();
            } catch ( IOException e ) {
              e.printStackTrace();
            }
          } else if ( running.compareAndSet( false, true ) ) {
            System.out.println( "Start Recording" );
            start.setText( "Stop" );
            rec = new SWTRecorder( recorderShell, Paths.get( "/tmp/abcd.swt" ) );
            rec.open();
          }
        }
      } );

      Button playback = new Button( recorderShell, SWT.NONE );
      playback.setText( "Playback" );
      props.setLook( playback );
      playback.setLayoutData( new FormDataBuilder().bottom().left().result() );
      playback.addSelectionListener( new SelectionAdapter() {
        @Override public void widgetSelected( SelectionEvent selectionEvent ) {
          super.widgetSelected( selectionEvent );
          SWTPlayback playback = new SWTPlayback(
            recorderShell.getDisplay(), Paths.get( "/tmp", "abcd.swt" ) );
          playback.open();
        }
      } );

      recorderShell.setLocation( 10, 10 );
      recorderShell.setSize( 100, 150 );
      recorderShell.open();
      //      recorderShell.setVisible( true );
    } );

  }

  @Override public void onEnvironmentShutdown() {

  }
}
