package org.hitachivantara.spoonrecorder.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class SaveSelectionListener implements SelectionListener {
  Shell parent;
  public SaveSelectionListener( Shell parent ){
    this.parent = parent;
  }

  @Override
  public void widgetSelected( SelectionEvent selectionEvent ) {
    FileDialog fd = new FileDialog( parent, SWT.SAVE);
    fd.setText("Save");
    fd.setFilterPath("C:/");
    String[] filterExt = {"*.txt","*.doc", ".rtf", "*.*"};
    fd.setFilterExtensions(filterExt);
    String selected = fd.open( );
    System.out.println(selected);
  }

  @Override
  public void widgetDefaultSelected( SelectionEvent selectionEvent ) {

  }
}
