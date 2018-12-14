package org.hitachivantara.spoonrecorder.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hitachivantara.spoonrecorder.SWTPlayback;
import org.hitachivantara.spoonrecorder.listeners.OpenSelectionListener;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class PlaybackSelectionAdapter extends SelectionAdapter {
  private Shell recorderShell;
  private OpenSelectionListener openSelectionListener;

  public PlaybackSelectionAdapter( Shell recorderShell, OpenSelectionListener openSelectionListener ) {
    this.recorderShell = recorderShell;
    this.openSelectionListener = openSelectionListener;
  }

  @Override
  public void widgetSelected( SelectionEvent event ) {
    super.widgetSelected( event );
    SWTPlayback playback;
    try {
      playback = new SWTPlayback(
          recorderShell.getDisplay(), Paths.get( openSelectionListener.getFilePath() ) );
      playback.open();
    } catch ( FileNotFoundException ex ) {
      MessageBox errorBox = new MessageBox( recorderShell, SWT.ICON_ERROR | SWT.OK );
      errorBox.setMessage( "No file loaded, please select playback file" );
      int response = errorBox.open();
    }
  }
}
