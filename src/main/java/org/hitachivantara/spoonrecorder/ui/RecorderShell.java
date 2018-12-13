package org.hitachivantara.spoonrecorder.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.hitachivantara.spoonrecorder.SWTRecorder;
import org.pentaho.di.ui.core.PropsUI;

import java.util.concurrent.atomic.AtomicBoolean;

public class RecorderShell {
  AtomicBoolean running = new AtomicBoolean( false );
  SWTRecorder rec;
  private Display display;

  public RecorderShell( Display display ) {
    this.display = display;
  }

  public void render() {
    PropsUI props = PropsUI.getInstance();

    Shell recorderShell = new Shell( display );
    recorderShell.setLayout( renderLayout() );

    renderTabFolder( recorderShell );
//    Button start = new Button( recorderShell, SWT.NONE );
//    start.setText( "Start" );
//    props.setLook( start );
//    start.setLayoutData( new FormDataBuilder().top().left().result() );
//
//    start.addSelectionListener( new SelectionAdapter() {
//      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
//        super.widgetSelected( selectionEvent );
//        if ( running.compareAndSet( true, false ) ) {
//          start.setText( "Start" );
//          System.out.println( "Stopping Recording" );
//          try {
//            rec.close();
//          } catch ( IOException e ) {
//            e.printStackTrace();
//          }
//        } else if ( running.compareAndSet( false, true ) ) {
//          System.out.println( "Start Recording" );
//          start.setText( "Stop" );
//          rec = new SWTRecorder( recorderShell, Paths.get( "/tmp/abcd.swt" ) );
//          rec.open();
//        }
//      }
//    } );
//
//    Button playback = new Button( recorderShell, SWT.NONE );
//    playback.setText( "Playback" );
//    props.setLook( playback );
//    playback.setLayoutData( new FormDataBuilder().bottom().left().result() );
//    playback.addSelectionListener( new SelectionAdapter() {
//      @Override public void widgetSelected( SelectionEvent selectionEvent ) {
//        super.widgetSelected( selectionEvent );
//        SWTPlayback playback = new SWTPlayback(
//            recorderShell.getDisplay(), Paths.get( "/tmp", "abcd.swt" ) );
//        playback.open();
//      }
//    } );
//
    recorderShell.setLocation( 10, 10 );
    recorderShell.setSize( 640, 400 );
    recorderShell.open();
  }

  private TabFolder renderTabFolder( Composite c ) {
    TabFolder tf = new TabFolder( c, SWT.BORDER );

    TabItem recordTab = new TabItem( tf, SWT.BORDER );
    recordTab.setText( "Recorder" );
    recordTab.setControl( new RecorderComposite( tf, SWT.BORDER ) );

    TabItem playbackTab = new TabItem( tf, SWT.BORDER );
    playbackTab.setText( "Playback" );
    playbackTab.setControl( new PlaybackComposite( tf, SWT.BORDER ) );

    return tf;
  }

  private FillLayout renderLayout() {
    FillLayout fl = new FillLayout(  );
    return fl;
  }
}
