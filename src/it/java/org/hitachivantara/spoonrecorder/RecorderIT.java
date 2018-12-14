package org.hitachivantara.spoonrecorder;


import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hitachivantara.spoonrecorder.plugin.RecorderPlugin;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.pan.CommandLineOption;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.OsHelper;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.delegates.SpoonDelegates;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class RecorderIT {


  private Display display = new Display();
  private Shell shell = new Shell( display );
  private PropsUI props;
  private ModifyListener modifyListener = ( p ) -> {
  };
  private Spoon spoon;

  @Before
  public void before() throws KettleException, NoSuchFieldException, IllegalAccessException {
    if ( Props.isInitialized() ) {
      Props.getInstance().reset();
    }
    PropsUI.init( display, "" );
    props = PropsUI.getInstance();

    Window.setDefaultModalParent( () -> shell );
    KettleClientEnvironment.init();
    PluginRegistry.addPluginType( StepPluginType.getInstance() );
    PluginRegistry.init();
    spoon = new Spoon();
    spoon.delegates = new SpoonDelegates( spoon );

    CommandLineOption[] commandLineArgs = Spoon.getCommandLineArgs( emptyList() );
    Field optField = Spoon.class.getDeclaredField( "commandLineOptions" );
    optField.setAccessible( true );
    optField.set( spoon, commandLineArgs );

    spoon.start( commandLineArgs );

  }

  @Test
  public void testRecord() throws KettleXMLException, KettleMissingPluginsException {
    OsHelper.initOsHandlers( display );
    spoon.open();
    try {
      spoon.openFile( "/Users/matcampbell/dev/mkambol/spoon-recorder/src/it/resources/testrails/T27131405/sample.ktr",
        false );
    } catch ( SWTError error ) {
    }

    try ( SWTRecorder rec = new SWTRecorder( shell, Paths.get( "/tmp", "abcd.swt" ) ) ) {
      rec.open();
      while ( spoon.getShell() != null && !spoon.getShell().isDisposed() ) {
        if ( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  @Test
  public void T27131405() throws InterruptedException {
    OsHelper.initOsHandlers( display );
    spoon.open();
    spoon.openFile(
      getResource( "testrails/T27131405/sample.ktr" ).getAbsolutePath(),
      false );

    try ( SWTPlayback rec = new SWTPlayback( shell.getDisplay(),
      getResource( "testrails/T27131405/tr.swt" ).toPath() ) ) {
      rec.open();

      while ( spoon.getShell() != null && !spoon.getShell().
        isDisposed() ) {
        if ( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
    }

  }

  @Test
  public void testPlayback() throws InterruptedException {
    OsHelper.initOsHandlers( display );
    spoon.open();
    try {
      spoon.openFile( getResource( "simple.ktr" ).getAbsolutePath(), false );
    } catch ( SWTError error ) {
    }

    try ( SWTPlayback rec = new SWTPlayback( shell.getDisplay(),
      Paths.get( getResource( "sequences/simple.swt" ).getAbsolutePath() ) ) ) {
      rec.open();

      while ( spoon.getShell() != null && !spoon.getShell().isDisposed() ) {
        if ( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
    }

  }


  @Test
  public void testPlugin() throws LifecycleException {
    OsHelper.initOsHandlers( display );
    spoon.open();
    spoon.openFile( getResource( "simple.ktr" ).getAbsolutePath(), false );
    RecorderPlugin plugin = new RecorderPlugin();
    plugin.onEnvironmentInit();

    while ( spoon.getShell() != null && !spoon.getShell().isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
  }

  private static TransMeta getTransMeta( String ktrName ) throws KettleXMLException, KettleMissingPluginsException {
    String file = getResource( ktrName ).getAbsolutePath();
    return new TransMeta( file );
  }

  private static File getResource( String resourceName ) {
    System.out.println( RecorderIT.class.getClassLoader().getResource( "" ).getPath() );

    return Optional.ofNullable( RecorderIT.class.getClassLoader().getResource( resourceName ) )
      .map( URL::getFile )
      .map( File::new )
      .orElseThrow( () -> new IllegalArgumentException( "can't find " + resourceName ) );
  }

}
