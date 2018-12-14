package org.hitachivantara.spoonrecorder.plugins;

import org.eclipse.swt.widgets.*;
import org.hitachivantara.spoonrecorder.ui.RecorderShell;
import org.pentaho.di.core.annotations.KettleLifecyclePlugin;
import org.pentaho.di.core.lifecycle.KettleLifecycleListener;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.ui.spoon.Spoon;

@KettleLifecyclePlugin ( id = "SpoonRecorder", name = "Spoon UI Recorder" )
public class RecorderPlugin implements KettleLifecycleListener {

  @Override public void onEnvironmentInit() throws LifecycleException {
      openRecorderShell();
  }

  @Override public void onEnvironmentShutdown() {

  }

  private void openRecorderShell() {
//    XulDomContainer container = Spoon.getInstance().getXulDomContainer();
//    container.registerClassLoader( getClass().getClassLoader() );
//    container.loadOverlay( "org/hitachivantara/spoonrecorder/menuItem.xul", new XulSpoonResourceBundle( RecorderPlugin.class ) );
//    Document doc = Spoon.getInstance().getXulDomContainer().getDocumentRoot();
//    //doc.addOverlay( "org/hitachivantara/spoonrecorder/menuItem.xul" );
//    XulMenu spoonToolsMenu = (XulMenu) doc.getElementById( "tools" );
//
//    XulLoader xulLoader = Spoon.getInstance().getXulDomContainer().getXulLoader();
//    XulMenuitem recorderItem = (XulMenuitem) xulLoader.createElement( "recorder-item" );
//    recorderItem.setLabel( "Recorder Thingy..." );
//    spoonToolsMenu.addChild( recorderItem );

    Display mainDisp = Spoon.getInstance().getShell().getDisplay();

    mainDisp.asyncExec( () -> {
      RecorderShell recorderShell = new RecorderShell( mainDisp );
      recorderShell.render();
    } );
  }
}
