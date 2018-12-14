package org.hitachivantara.spoonrecorder.adapters;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.hitachivantara.spoonrecorder.SWTPlayback;

import java.nio.file.Paths;

public class PlaybackSelectionAdapter extends SelectionAdapter {
  private Shell recorderShell;

  public PlaybackSelectionAdapter( Shell recorderShell ) {
    this.recorderShell = recorderShell;
  }

  @Override
  public void widgetSelected( SelectionEvent event ) {
    super.widgetSelected( event );
    SWTPlayback playback = new SWTPlayback(
        recorderShell.getDisplay(), Paths.get( "/tmp", "abcd.swt" ) );
    playback.open();
  }
}
