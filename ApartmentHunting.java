
import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import javax.imageio.*;
import java.awt.*;
import java.io.File;
import java.net.*;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import com.esri.mo2.ui.bean.*; 
import com.esri.mo2.ui.dlg.AboutBox;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.cs.geom.Envelope;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.map.draw.*;
import com.esri.mo2.ui.bean.Tool;
import java.util.ArrayList;


public class ApartmentHunting extends JFrame {
  ResourceBundle names;
  Locale loc1 = new Locale("es","MX"); 
  Locale loc2 = new Locale("en","US");
  static Map map = new Map();
  static boolean fullMap = true;
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = new Layer();
  Layer layer4 = new Layer();
  Layer layer5 = new Layer();
  Layer layer6 = null;
  static com.esri.mo2.map.dpy.Layer layer7;
  com.esri.mo2.map.dpy.Layer activeLayer;
  int activeLayerIndex;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenu theme = new JMenu("Theme");
  //#2
  JMenu help = new JMenu("Help");
  JMenu language = new JMenu("Language");
  JMenu layercontrol = new JMenu("Layer Control");

  JMenuItem englishjb = new JMenuItem("English");
  JMenuItem spanishjb = new JMenuItem("Espa\u00F1ol");
  
  JMenuItem attribitem = new JMenuItem("Open Attribute Table",new ImageIcon("tableview.gif"));
  JMenuItem createlayeritem  = new JMenuItem("Create Layer From Selection",new ImageIcon("Icon0915b.jpg"));
  static JMenuItem promoteitem = new JMenuItem("Promote Selected Layer",new ImageIcon("promote1.gif"));
  JMenuItem demoteitem = new JMenuItem("Demote Selected Layer", new ImageIcon("demote1.gif"));
  JMenuItem printitem = new JMenuItem("Print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("Add Layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("Remove Layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  //#2
  JMenuItem readmeitem = new JMenuItem("Readme",new ImageIcon("readme.gif"));
  JMenuItem aboutitem = new JMenuItem("About",new ImageIcon("about.gif"));
  JMenuItem contactitem = new JMenuItem("Contact Us",new ImageIcon("contact.gif"));
  Toc toc = new Toc();

  //Code for adding roadmap **EXPERIMENTAL**
  //String s1 = "C:\\ESRI\\MOJ20\\Project\\Subdivision\\SUBDIVISION.shp";
  //String s1 = "C:\\ESRI\\MOJ20\\Project\\Subdivision_Line\\SUBDIVISION_LINE.shp";
  //String s1 = "C:\\ESRI\\MOJ20\\Project\\ZONING\\ZONING_BASE_SD.shp";
  //String s1 = "C:\\ESRI\\MOJ20\\Project\\Roads\\CITY_ROADS_ALL.shp";
  //END OF ROADMAP CODE

  String s1 = "Data\\sdcounty\\SanDiegoCity.shp";
  String s2 = "Data\\SDSU\\SDSU.shp";
  String s3 = "Data\\UCSD\\UCSD.shp";
  String s4 = "Data\\SDSU_A\\SDSU_Apartments.shp";
  String s5 = "Data\\UCSD_A\\UCSD_Apartments.shp";
  String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  ComponentListener complistener;
  JLabel statusLabel = new JLabel("status bar    LOC");
  java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton prtjb = new JButton(new ImageIcon("icons/print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon("icons/addtheme.gif"));
  JButton ptrjb = new JButton(new ImageIcon("icons/pointer.gif"));
  JButton hotjb = new JButton(new ImageIcon("icons/hotlink.gif"));
  Arrow arrow = new Arrow();
  ActionListener lis;
  ActionListener layerlis;
  ActionListener layercontrollis;
  ActionListener helplis;
  ActionListener lang;
  TocAdapter mytocadapter;
  Toolkit tk = Toolkit.getDefaultToolkit();
  Image bolt = tk.getImage("icons/hotlink_32x32-32.gif");  // 16x16 gif file
  java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new java.awt.Point(11,26),"bolt");
  MyPickAdapter picklis = new MyPickAdapter();
  Pick hotlink = new Pick(); //the Identify class implements a PickListener,
  static String myApt = null;
  class MyPickAdapter implements PickListener {   //implements hotlink
    public void beginPick(PickEvent pe){};
    // this fires even when you click outside the states layer
    public void endPick(PickEvent pe){}
    public void foundData(PickEvent pe){
      //fires only when a layer feature is clicked
      FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
      com.esri.mo2.data.feat.Cursor c = pe.getCursor();
      Feature f = null;
      System.out.println("inside foundData");
      Fields fields = null;
      if (c != null)
        f = (Feature)c.next();
      fields = f.getFields();
      String sname = fields.getField(1).getName(); //gets col. name for state name
      myApt = (String)f.getValue(1);
      System.out.println(myApt);
      try {
            HotPick hotpick = new HotPick();//opens dialog window with image in it
            hotpick.setVisible(true);
      } catch(Exception e){}
    }
  };

  static Envelope env;
  public ApartmentHunting() {
    super("Apartment Hunting");
    this.setBounds(150,150,900,650);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);
    ActionListener lisZoom = new ActionListener() {
      public void actionPerformed(ActionEvent ae){
        fullMap = false;}}; // can change a boolean here
    ActionListener lisFullExt = new ActionListener() {
      public void actionPerformed(ActionEvent ae){
        fullMap = true;}};
    // next line gets ahold of a reference to the zoomin link
    JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
    JButton zoomFullExtentButton = (JButton)zptb.getActionComponent("ZoomToFullExtent");
    JButton zoomToSelectedLayerButton = (JButton)zptb.getActionComponent("ZoomToSelectedLayer");
    zoomInButton.addActionListener(lisZoom);
    zoomFullExtentButton.addActionListener(lisFullExt);
    zoomToSelectedLayerButton.addActionListener(lisZoom);
    complistener = new ComponentAdapter () {
      public void componentResized(ComponentEvent ce) {
        if(fullMap) {
          map.setExtent(env);
          map.zoom(1.0);    //scale is scale factor in pixels
          map.redraw();
        }
      }
    };
    addComponentListener(complistener);
    lis = new ActionListener() {public void actionPerformed(ActionEvent ae){
      Object source = ae.getSource();
      if (source == prtjb || source instanceof JMenuItem ) {
        com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
        mapPrint.setMap(map);
        mapPrint.doPrint();// prints the map
      }
      else if (source == ptrjb) {
            map.setSelectedTool(arrow);
      }
      else if (source == hotjb) {
                hotlink.setCursor(boltCursor);
        map.setSelectedTool(hotlink);
      }
      else {
            try {
              AddLyrDialog aldlg = new AddLyrDialog();
              aldlg.setMap(map);
              aldlg.setVisible(true);
            } catch(IOException e){}
      }
    }};
    //Language change code
lang = new ActionListener() { //change 8
          public void actionPerformed(ActionEvent ae){
                  Object source = ae.getSource();
                  if(source instanceof JMenuItem){
                  if (source == englishjb) {
                        names = ResourceBundle.getBundle("NamesBundle",loc2);  //   (2)
                        java.util.List list = toc.getAllLegends();
                        int count = list.size();
                        for (int j =0;j<count;j++) {              //remove old layers
                           com.esri.mo2.map.dpy.Layer dpylayer1 =
                                    (com.esri.mo2.map.dpy.Layer) ((Legend)list.get(j)).getLayer();
                           map.getLayerset().removeLayer(dpylayer1);
                    }
                    addShapefileToMap(layer,s1);
                    addShapefileToMap(layer2,s2);
                    addShapefileToMap(layer3,s3);
                    addShapefileToMap(layer4,s4);
                    addShapefileToMap(layer5,s5);
             translate();

             System.out.println("Reached");
              }
              else if (source == spanishjb) {
            names = ResourceBundle.getBundle("NamesBundle",loc1);
            java.util.List list = toc.getAllLegends();
            int count = list.size();
            for (int j =0;j<count;j++) {              //remove old layers
              com.esri.mo2.map.dpy.Layer dpylayer1 =
                            (com.esri.mo2.map.dpy.Layer) ((Legend)list.get(j)).getLayer();
                          map.getLayerset().removeLayer(dpylayer1);
                    }
                     addShapefileToMap(layer,s1);
                    addShapefileToMap(layer2,s2);
                    addShapefileToMap(layer3,s3);
                    addShapefileToMap(layer4,s4);
                    addShapefileToMap(layer5,s5);
             translate();
               
             System.out.println("Reached");   }
    }
    }

  };

    layercontrollis = new ActionListener() {public void actionPerformed(ActionEvent ae){
         Object source = ae.getSource();
         System.out.println(activeLayerIndex+" active index");
         if (source == promoteitem)
           map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
         else
           map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
         enableDisableButtons();
         map.redraw();
    }};
    layerlis = new ActionListener() {public void actionPerformed(ActionEvent ae){
      Object source = ae.getSource();
      if (source instanceof JMenuItem) {
        if(source==addlyritem) {
          try {
            AddLyrDialog aldlg = new AddLyrDialog();
            aldlg.setMap(map);
            aldlg.setVisible(true);
          } catch(IOException e){}
            }
            else if(source == remlyritem) {
              try {
                com.esri.mo2.map.dpy.Layer dpylayer =
                      legend.getLayer();
                map.getLayerset().removeLayer(dpylayer);
                map.redraw();
                remlyritem.setEnabled(false);
                propsitem.setEnabled(false);
                attribitem.setEnabled(false);
                promoteitem.setEnabled(false);
                demoteitem.setEnabled(false);
                stb.setSelectedLayer(null);
                zptb.setSelectedLayer(null);
                stb.setSelectedLayers(null);
              } catch(Exception e) {}
            }
            else if(source == propsitem) {
          LayerProperties lp = new LayerProperties();
           lp.setLegend(legend);
           lp.setSelectedTabIndex(0);
           lp.setVisible(true);
            }
            else if (source == attribitem) {
              try {
                layer7 = legend.getLayer();
              AttrTab attrtab = new AttrTab();
              attrtab.setVisible(true);
              } catch(IOException ioe){}
            }
        else if (source==createlayeritem) {
              BaseSimpleRenderer sbr = new BaseSimpleRenderer();
              SimplePolygonSymbol sps = new SimplePolygonSymbol();
              sps.setPaint(AoFillStyle.getPaint(
                      AoFillStyle.SOLID_FILL,new java.awt.Color(255,255,0)));
              sps.setBoundary(true);
              layer7 = legend.getLayer();
              FeatureLayer flayer2 = (FeatureLayer)layer7;
              // select, e.g., Montana and then click the
              // create layer menuitem; next line verifies a selection was made
              System.out.println("has selected" + flayer2.hasSelection());
              //next line creates the 'set' of selections
              if (flayer2.hasSelection()) {
                SelectionSet selectset = flayer2.getSelectionSet();
                // next line makes a new feature layer of the selections
                FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
                sbr.setLayer(selectedlayer);
                sbr.setSymbol(sps);
                selectedlayer.setRenderer(sbr);
                Layerset layerset = map.getLayerset();
                // next line places a new visible layer, e.g. Montana, on the map
                layerset.addLayer(selectedlayer);
                //selectedlayer.setVisible(true);
                if(stb.getSelectedLayers() != null)
                  promoteitem.setEnabled(true);
                try {
                  legend2 = toc.findLegend(selectedlayer);
                } catch (Exception e) {}

                CreateShapeDialog csd = new CreateShapeDialog(selectedlayer);
                csd.setVisible(true);
                Flash flash = new Flash(legend2);
                flash.start();
                map.redraw(); // necessary to see color immediately
              }
            }
      }
    }};
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
      public void click(TocEvent e) {
            legend = e.getLegend();
        activeLayer = legend.getLayer();
        stb.setSelectedLayer(activeLayer);
        zptb.setSelectedLayer(activeLayer);
        // get active layer index for promote and demote
        activeLayerIndex = map.getLayerset().indexOf(activeLayer);
        // layer indices are in order added, not toc order.
        com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
        hotlink.setLayer(activeLayer);// replaces setToc from MOJ10
        remlyritem.setEnabled(true);
        propsitem.setEnabled(true);
        attribitem.setEnabled(true);
        enableDisableButtons();
      }
    };
    map.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent me) {
        com.esri.mo2.cs.geom.Point worldPoint = null;
        if (map.getLayerCount() > 0) {
          worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
          String s = "X:"+df.format(worldPoint.getX())+" "+
                     "Y:"+df.format(worldPoint.getY());
          statusLabel.setText(s);
        }
        else
          statusLabel.setText("X:0.000 Y:0.000");
      }
    });
    //#2
    helplis = new ActionListener()
    {public void actionPerformed(ActionEvent ae){
      Object source = ae.getSource();
      if(source instanceof JMenuItem){
        if(source==aboutitem){
          AboutBox aboutbox = new AboutBox();
          aboutbox.setProductName("Apartment Hunting");
          aboutbox.setProductVersion("1.0");
          aboutbox.setVisible(true);
         
        }
        else if(source==contactitem){
         try{
           String s ="\n\n\n Any enquires"+" Dont Bother!!!!";
           HelpDialog helpdialog = new HelpDialog(s);
           helpdialog.setVisible(true);

         }
         catch(IOException e){}
      }
      else if(source==readmeitem) {
          try{
            File readMeFile = new File("Readme.txt");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().edit(readMeFile);
            } else {
                // dunno, up to you to handle this
            }
              
           }
                catch(IOException e)
           {           }
           
      }
     
    }

    }};

   
    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    attribitem.setEnabled(false);
    promoteitem.setEnabled(false);
    demoteitem.setEnabled(false);
    printitem.addActionListener(lis);
    addlyritem.addActionListener(layerlis);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    attribitem.addActionListener(layerlis);
    createlayeritem.addActionListener(layerlis);
    promoteitem.addActionListener(layercontrollis);
    demoteitem.addActionListener(layercontrollis);
    //#2
    englishjb.addActionListener(lang);
    spanishjb.addActionListener(lang);
    contactitem.addActionListener(helplis);
    readmeitem.addActionListener(helplis);
    aboutitem.addActionListener(helplis);
    file.add(addlyritem);
    file.add(printitem);
    file.add(remlyritem);
    file.add(propsitem);
    theme.add(attribitem);
    theme.add(createlayeritem);
    layercontrol.add(promoteitem);
    layercontrol.add(demoteitem);
    language.add(englishjb);
    language.add(spanishjb);

    
    //#2
    help.add(readmeitem);
    help.add(aboutitem);
    help.add(contactitem);
    mbar.add(file);
    mbar.add(theme);
    mbar.add(layercontrol);
    //#2
    mbar.add(help);
    mbar.add(language);
    prtjb.addActionListener(lis);
    prtjb.setToolTipText("print map");
    addlyrjb.addActionListener(lis);
    addlyrjb.setToolTipText("add layer");
    hotlink.addPickListener(picklis);
    hotlink.setPickWidth(7);// sets tolerance for hotlink clicks
    hotjb.addActionListener(lis);
    hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
    ptrjb.addActionListener(lis);
    ptrjb.setToolTipText("pointer");
    jtb.add(prtjb);
    jtb.add(addlyrjb);
    jtb.add(ptrjb);
    jtb.add(hotjb);
    myjp.add(jtb);
    myjp.add(zptb);
    myjp.add(stb);
    myjp2.add(statusLabel);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    addShapefileToMap(layer3,s3);
    addShapefileToMap(layer4,s4);
    addShapefileToMap(layer5,s5);
    getContentPane().add(toc, BorderLayout.WEST);

    //*6
    java.util.List list1 = toc.getAllLegends();

    FeatureLayer flayer = (FeatureLayer) ((Legend)list1.get(4)).getLayer();
    BaseSimpleRenderer bsr = (BaseSimpleRenderer)flayer.getRenderer();
    Symbol sym = bsr.getSymbol();  //returns class Symbol
    SimplePolygonSymbol sps = (SimplePolygonSymbol)sym;
    sps.setFillTransparency(.5);
    bsr.setSymbol(sps);
    flayer = (FeatureLayer) ((Legend)list1.get(3)).getLayer();
    bsr = (BaseSimpleRenderer)flayer.getRenderer();
    sym = bsr.getSymbol();
    sps = (SimplePolygonSymbol)sym;
    sps.setFillTransparency(.5);
    bsr.setSymbol(sps);
     flayer = (FeatureLayer) ((Legend)list1.get(2)).getLayer();
    bsr = (BaseSimpleRenderer)flayer.getRenderer();
    sym = bsr.getSymbol();
    sps = (SimplePolygonSymbol)sym;
    sps.setFillTransparency(.5);
    bsr.setSymbol(sps);


  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ10\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  public static void main(String[] args) {
    ApartmentHunting qstart = new ApartmentHunting();
    qstart.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.out.println("Thanks, Quick Start exits");
        System.exit(0);
      }
    });
    qstart.setVisible(true);
    env = map.getExtent();
  }

private void translate() {                              //    change 9s
        file.setText(names.getString("File"));
        addlyritem.setText(names.getString("addLayer"));
        remlyritem.setText(names.getString("RemoveLayer"));
        printitem.setText(names.getString("Print"));
        propsitem.setText(names.getString("LegendEditor"));
        //remlyritem.setText(names.getString("RemoveLayer"));
        theme.setText(names.getString("Theme"));
        attribitem.setText(names.getString("OpenAttributeTable"));
        createlayeritem.setText(names.getString("CreateLayerFromSelection"));

        layercontrol.setText(names.getString("LayerControl"));
        promoteitem.setText(names.getString("PromoteItem"));
        demoteitem.setText(names.getString("DemoteItem"));

        aboutitem.setText(names.getString("AboutUs"));
        readmeitem.setText(names.getString("Readme"));
        contactitem.setText(names.getString("Contact"));


        prtjb.setToolTipText(names.getString("Print"));
        //addlyrjb.setToolTipText(names.getString("AddLayer"));

        //file.setText(names.getString("Archivo"));
  }
  private void enableDisableButtons() {
    int layerCount = map.getLayerset().getSize();
    if (layerCount < 2) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(false);
      }
    else if (activeLayerIndex == 0) {
      demoteitem.setEnabled(false);
      promoteitem.setEnabled(true);
          }
    else if (activeLayerIndex == layerCount - 1) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(true);
    }
    else {
      promoteitem.setEnabled(true);
      demoteitem.setEnabled(true);
    }
  }
}
// following is an Add Layer dialog window
class AddLyrDialog extends JDialog {
  Map map;
  ActionListener lis;
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JPanel panel1 = new JPanel();
  com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.
    CustomDatasetEditor();
  AddLyrDialog() throws IOException {
    setBounds(50,50,520,430);
    setTitle("Select a theme/layer");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        setVisible(false);
      }
    });
    lis = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == cancel)
          setVisible(false);
        else {
          try {
            setVisible(false);
            map.getLayerset().addLayer(cus.getLayer());
            map.redraw();
            if (ApartmentHunting.stb.getSelectedLayers() != null){
               ApartmentHunting.promoteitem.setEnabled(true);}
          } catch(IOException e){}
        }
      }
    };
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    getContentPane().add(cus,BorderLayout.CENTER);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
    map = map1;
  }
}
class AttrTab extends JDialog {
  JPanel panel1 = new JPanel();
  com.esri.mo2.map.dpy.Layer layer = ApartmentHunting.layer7;
  JTable jtable = new JTable(new MyTableModel());
  JScrollPane scroll = new JScrollPane(jtable);
  public AttrTab() throws IOException {
    setBounds(70,70,450,350);
    setTitle("Attribute Table");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        setVisible(false);
      }
    });
    scroll.setHorizontalScrollBarPolicy(
           JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    // next line necessary for horiz scrollbar to work
    jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableColumn tc = null;
    int numCols = jtable.getColumnCount();
    //jtable.setPreferredScrollableViewportSize(
        //new java.awt.Dimension(440,340));
    for (int j=0;j<numCols;j++) {
      tc = jtable.getColumnModel().getColumn(j);
      tc.setMinWidth(50);
    }
    getContentPane().add(scroll,BorderLayout.CENTER);
  }
}
class MyTableModel extends AbstractTableModel {
 // the required methods to implement are getRowCount,
 // getColumnCount, getValueAt
  com.esri.mo2.map.dpy.Layer layer = ApartmentHunting.layer7;
  MyTableModel() {
    qfilter.setSubFields(fields);
    com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
    while (cursor.hasMore()) {
      ArrayList inner = new ArrayList();
      Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
      inner.add(0,String.valueOf(row));
      for (int j=1;j<fields.getNumFields();j++) {
        inner.add(f.getValue(j).toString());
      }
      data.add(inner);
      row++;
    }
  }
  FeatureLayer flayer = (FeatureLayer) layer;
  FeatureClass fclass = flayer.getFeatureClass();
  String columnNames [] = fclass.getFields().getNames();
  ArrayList data = new ArrayList();
  int row = 0;
  int col = 0;
  BaseQueryFilter qfilter = new BaseQueryFilter();
  Fields fields = fclass.getFields();
  public int getColumnCount() {
    return fclass.getFields().getNumFields();
  }
  public int getRowCount() {
    return data.size();
  }
  public String getColumnName(int colIndx) {
    return columnNames[colIndx];
  }
  public Object getValueAt(int row, int col) {
    ArrayList temp = new ArrayList();
    temp =(ArrayList) data.get(row);
    return temp.get(col);
  }
}
class CreateShapeDialog extends JDialog {
  String name = "";
  String path = "";
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JTextField nameField = new JTextField("enter layer name here, then hit ENTER",25);
  com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
  ActionListener lis = new ActionListener() {public void actionPerformed(ActionEvent ae) {
    Object o = ae.getSource();
    if (o == nameField) {
      name = nameField.getText().trim();
      path = ((ShapefileFolder)(ApartmentHunting.layer7.getLayerSource())).getPath();
      System.out.println(path+"    " + name);
    }
    else if (o == cancel)
      setVisible(false);
    else {
      try {
        ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,1);
      } catch(Exception e) {System.out.println("write error");}
      setVisible(false);
    }
  }};

  JPanel panel1 = new JPanel();
  JLabel centerlabel = new JLabel();
  //centerlabel;
  CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5) {
        selectedlayer = layer5;
    setBounds(40,350,450,150);
    setTitle("Create new shapefile?");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
    });
    nameField.addActionListener(lis);
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
      "the new name you want for the layer and click OK.<BR>" +
      "You can then add it to the map in the usual way.<BR>"+
      "Click ENTER after replacing the text with your layer name";
    centerlabel.setHorizontalAlignment(JLabel.CENTER);
    centerlabel.setText(s);
    getContentPane().add(centerlabel,BorderLayout.CENTER);
    panel1.add(nameField);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
}
class Arrow extends Tool {
  public void mouseClicked(MouseEvent me){
  }
}
class Flash extends Thread {
  Legend legend;
  Flash(Legend legendin) {
    legend = legendin;
  }
  public void run() {
    for (int i=0;i<12;i++) {
      try {
         Thread.sleep(500);
        legend.toggleSelected();
      } catch (Exception e) {}
    }
  }
}

class HotPick extends JDialog {
  String myApt = ApartmentHunting.myApt;
  String myUni = null;
  String aptLink = null;
  String myAptPic = null;
  JPanel jpanel = new JPanel();
  JPanel jpanel2 = new JPanel();
  JPanel jpanel3 = new JPanel();
  //Copy Apartment List
  String[][] apartmentList={{"Dorado Plaza","http://www.doradoplazaapts.com/","Images/dorado.jpg",""},
  {"The Suites on Paseo","http://www.suitesonpaseo.com/","Images/theplaza.jpg",""},
  {"Sterling Alvarado","http://www.sterling-alvarado.com/","Images/sterlingalvarado.jpg",""},
  {"5025 Apartments","http://www.live5025.com/","Images/5025apt.jpg",""},
  {"BLVD63 Apartments","http://www.carmelapartments.com/blvd63","Images/BLVD63.jpg",""},
  {"College Campanile Apartments","http://campanilebysdsu.com/","Images/collegecampanile.jpg",""},
  {"The Plaza Apartments","http://plazabysdsu.com/","Images/theplaza.jpg",""},
  {"The Corinthian Apartments","http://www.corinthianapartmenthomes.com/","Images/corinthian.jpg",""},
  {"Villa Del Sol Apartments","http://www.villadelsolapts.com/","Images/villadelsol.jpg",""},
  {"College View Apartments","https://www.apartments.com/college-view-apartments-san-diego-ca/sweezm5/","Images/collegeview.jpg",""},
  {"The Aztec Pacific Apartments","http://pacificliving.com/property/aztec-pacific/","Images/aztecpacific.jpg",""},
  {"The Diplomat","http://diplomatbysdsu.com/","Images/thediplomat.jpg",""},
  {"The Dorchester","http://dorchesterbysdsu.com/","Images/dorchester.jpg",""},
  {"Penthouse Apartments","http://penthousebysdsu.com/","Images/penthouse.jpg",""},
  {"La Jolla Palms Apartments","https://www.irvinecompanyapartments.com/communities/la-jolla-palms","Images/lajollapalms.jpg",""},
  {"Nobel Court Apartments","http://www.nobelcourt.com/","Images/nobelcourt.jpg",""},
  {"Axiom La Jolla Apartments","http://www.axiomlajolla.com/","Images/axiomlajolla.jpg",""},
  {"La Scala Apartments","http://www.thepremiereresidential.com/properties/san-diego/la-scala/","Images/lascala.jpg",""},
  {"Regents La Jolla","http://www.regentslajolla.net/","Images/regentslajolla.jpg",""},
  {"La Jolla International Gardens","http://www.thepremiereresidential.com/properties/san-diego/la-jolla-international-gardens/","Images/lajollaintl.jpg",""},
  {"Costa Verde Village Apartments","http://www.apartmentguide.com/apartments/California/San-Diego/Costa-Verde-Village/6404/","Images/costaverde.jpg"},
  {"La Regencia","http://www.gardencommunitiesca.com/Apartment-Rentals/CA/San-Diego/La-Regencia.aspx","Images/laregencia.jpg",""},
  {"Towers at Costa Verde","http://www.gardencommunitiesca.com/Apartment-Rentals/CA/San-Diego/Towers-at-Costa-Verde.aspx","Images/towersatcostaverde.jpg",""},
  {"Solazzo Apartment Homes","https://www.irvinecompanyapartments.com/communities/solazzo","Images/solazzo.jpg",""}
};
  
    private static void open(URI uri) {
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(uri);
      } catch (IOException e) { /* TODO: error handling */ }
    } else { /* TODO: error handling */ }
  }

 
  HotPick() throws IOException {
        setTitle("This was your pick");
        

    setBounds(250,250,350,350);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
    });
    if(myApt.equals("San Diego State University"))
    {
            myUni = "San Diego State University";
            myAptPic = "Images/SDSU.jpg";
            aptLink = "https://www.sdsu.edu/";
            myApt=null;
    }
    else if(myApt.equals("UCSD"))
    {
            myUni = "University of California, San Diego";
            myAptPic = "Images/UCSD.png";
            aptLink = "https://ucsd.edu/";
            myApt=null;
    }
    else if(myApt.equals("San Diego"))
    {
            myUni = "San Diego City";
            myAptPic = "Images/SD.jpg";
            aptLink = "https://www.sandiego.gov";
            myApt=null;
    }
    else
      {    for (int i = 0;i<24;i++)  {
          if (apartmentList[i][0].equals(myApt)) {
            myUni = ":   " +apartmentList[i][3];
            myAptPic = apartmentList[i][2];
            aptLink =  apartmentList[i][1];
            break;
          }
    }
  }
  try 
        {
    JLabel label = new JLabel(myApt);
    JLabel label2 = new JLabel(myUni);
    JButton link = new JButton();
    

          Image image;  //this generates an image file
    image = ImageIO.read(new File(myAptPic));
    image.getScaledInstance(40,40,Image.SCALE_DEFAULT);
    ImageIcon aptIcon = new ImageIcon(image); 
    JLabel aptLabel = new JLabel(aptIcon);
    
    link.setText(aptLink);
    link.setHorizontalAlignment(SwingConstants.LEFT);
    link.setBorderPainted(false);
    link.setOpaque(false);
    link.setBackground(Color.WHITE);
    link.setToolTipText(aptLink.toString());
    link.addActionListener(new OpenUrlAction());
    jpanel.add(label);
    jpanel.add(label2);
    jpanel2.add(aptLabel);
    jpanel3.add(link);
    getContentPane().add(jpanel,BorderLayout.NORTH);
    getContentPane().add(jpanel2,BorderLayout.CENTER);
    getContentPane().add(jpanel3,BorderLayout.SOUTH);
    }
    catch (IOException e)
        {e.printStackTrace();}
    
  }

   class OpenUrlAction implements ActionListener{
      @Override public void actionPerformed(ActionEvent e) {
        try
        {
        URI uri = new URI(aptLink);
         open(uri);
        }
        catch(URISyntaxException ue)
        {}
       
      }
    }

}



//Remaining
class HelpDialog extends JDialog {      //change 1
 JTextArea helptextarea;
 public HelpDialog(String inputText) throws IOException {
    setBounds(70,70,450,250);
    setTitle("Help");
    addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e)
      {
        setVisible(false);
      }
    });
    inputText="This is an application to view the apartments located near San Diego State University and University of California, San Diego for college students.\n We can explore the companies:";
helptextarea = new JTextArea (inputText,7,40);
  JScrollPane scrollpane = new JScrollPane(helptextarea);
helptextarea.setEditable(false);
getContentPane().add(scrollpane,"Center");
//setuphelpText();
 }
}

