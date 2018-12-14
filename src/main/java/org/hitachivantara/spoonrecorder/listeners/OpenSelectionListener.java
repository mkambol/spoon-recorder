package org.hitachivantara.spoonrecorder.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenSelectionListener implements SelectionListener {
  private Shell parent;

  public OpenSelectionListener( Shell parent ) {
    this.parent = parent;
  }

  @Override
  public void widgetSelected( SelectionEvent selectionEvent ) {
    FileDialog fd = new FileDialog( parent, SWT.OPEN );
    fd.setText("Open");
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
