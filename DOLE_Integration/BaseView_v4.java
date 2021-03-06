import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.java.actionlogger.ActionLogger;
//import java.io.IOException;
//import org.json.simple.parser.ParseException;
//import com.java.actionloggerexception.CryptoException;

import javax.swing.BoxLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.util.*;

public class BaseView_v4 {

	private JFrame frame;
	static JTextField textField = new JTextField(30);
	static JPanel jPanel3=new JPanel();
	static JScrollPane jSPane = null;
	static final HighlightTreeCellRenderer renderer = new HighlightTreeCellRenderer();
	
	static double width;
	static double height;
	
	static String database;
	static String host;
	static String passwd;
	static String query=null;
	
	static ArrayList<String> bugList= new ArrayList<String>();
	
	static Vector<String> bugNames = new Vector<String>();
	
	static Connection connection=null;
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) throws Exception{
		ActionLogger.log("DOLE_APP_TEST");		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		JFrame frame = new JFrame("List of Assignments");
		//Holds JTree and SearchBox
		JPanel jPanel1=new JPanel();
		//Holds file display
		JPanel jPanel2=new JPanel();
		
		JScrollPane jsp = null;
		
		// Making root tree for repositories
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode dmtn_pm = null;
        DefaultMutableTreeNode dmtn_tag = null;
        DefaultMutableTreeNode dmtn_label = null;
        ArrayList<DefaultMutableTreeNode> expanded_nodes= new ArrayList<DefaultMutableTreeNode>();
        
        // Making list of programming model
        String[] programmingModels = {"MPI","OPENMP","CUDA"};
        
        database = args[0];
		host = args[1];
		if (args.length < 3)
			passwd = "";
		else
			passwd = args[2];
		    
		jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
		jPanel1.setMaximumSize(new Dimension(10, 20));
		jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));
		frame.setSize(800, 600);
		frame.setResizable(true);
		width = screenSize.getWidth();
		height = screenSize.getHeight();
		
		// Making the bug tree
        for (String model : programmingModels) {
        	dmtn_pm = new DefaultMutableTreeNode(model);
            for (String tag : getTag(model)) {
            	dmtn_tag=new DefaultMutableTreeNode(tag);
                for (String label : getBugName(model,tag)) {
                	dmtn_label = new DefaultMutableTreeNode(label);
                    dmtn_tag.add(dmtn_label);
                }
                dmtn_pm.add(dmtn_tag);
            }
            root.add(dmtn_pm);
        }
        
        JTree jTree = new JTree(root);
		jTree.setShowsRootHandles(true);
        jTree.setRootVisible(false);
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.setFont(new Font("Arial",Font.BOLD,(int) width/60));
		
		
		jsp=new JScrollPane(jTree);
		jsp.setPreferredSize(new Dimension(300, 500));
		
		jPanel1.add(jsp);
		
		textField.setText("Search bug category, press Enter");
		textField.setFont(new Font("Arial",Font.CENTER_BASELINE,(int) width/120));
		textField.addMouseListener(new MouseAdapter() {
		
			@Override
		    public void mouseClicked(MouseEvent e) {
				textField.setText("");
				//jPanel1.remove(comp);
				for(DefaultMutableTreeNode node:expanded_nodes) {
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
					jTree.collapsePath(new TreePath(node.getPath()));
					jTree.collapsePath(new TreePath(parentNode.getPath()));
				}
				if (jSPane!=null) {
					jPanel1.remove(jSPane);
					jPanel1.repaint();
				}
			}
		});
		
		textField.addActionListener(new ActionListener(){
			
			ArrayList<DefaultMutableTreeNode> nodes=new ArrayList<DefaultMutableTreeNode>();
			Enumeration<TreeNode> en = null;
			String element=null;

			public void actionPerformed(ActionEvent e){
				
				nodes=getLeafNodes(root);
                bugList = getSearchTags(textField.getText(),  database,  host, passwd);
                
                for(String str: bugList){
                	for(DefaultMutableTreeNode node:nodes) {
                		en=node.preorderEnumeration();
                    	while (en.hasMoreElements()) {
                    		element=en.nextElement().toString();
                    			if(element.equals(str)) {
                    				jTree.expandPath(new TreePath(node.getPath()));
                    				expanded_nodes.add(node);
                    			}
                    	}
                    }
				}
                
                jPanel3=getsearchPanel(textField.getText(), database, host, passwd, jTree);
                jSPane=new JScrollPane(jPanel3);
                jSPane.setPreferredSize(new Dimension(300, 300));
                jPanel1.add(jSPane);
        		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        	    jPanel1.repaint();
        	    jPanel1.revalidate();
            }
		});
			
		jPanel1.add(textField);
		frame.add(jPanel1, BorderLayout.WEST);
			
		jTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			String repo = null;
			String path = null;
			
			public void valueChanged(TreeSelectionEvent e) {
				
				repo=jTree.getLastSelectedPathComponent().toString();
				if(repo==null) return;
		        path=getPath(repo);
		        if(path==null) return;
		        modifyFrame(path, jPanel1, textField, jPanel2, frame, database, host, passwd);
		        frame.setVisible(true);
			}
		});
		jTree.setCellRenderer(renderer);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        System.exit(0);
		    }
		});		
	}
	
	static ResultSet fetchDB(String query) {
		
		ResultSet resultSet=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database+"?useUnicode=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",host,passwd);
			resultSet=connection.createStatement().executeQuery(query);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultSet;
	}
	
	static Vector<String> getBugName(String model, String tag) {
		
		bugNames.removeAllElements();
		query = "select distinct bugCollection.name from bugRecord join programmingModel on pm_id = programmingModel.id join bugCatagories on Cate_id = bugCatagories.id join bugCollection on Bug_id = bugCollection.id where programmingModel.name = \""+ model+"\" and bugCatagories.name = \"" + tag+"\"";
		ResultSet resultSet=fetchDB(query);
        try {
			while(resultSet.next())
				bugNames.add(resultSet.getString(1));
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return bugNames;
    }
	
	public static Vector<String> getTag (String model) {
        
		Vector<String> tags = new Vector<String>();
		query = "select distinct bugCatagories.name from bugRecord join programmingModel on pm_id = programmingModel.id join bugCatagories on Cate_id = bugCatagories.id  where programmingModel.name =\""+ model+"\"";
		ResultSet resultSet=fetchDB(query);
        try {
			while(resultSet.next())
				tags.add(resultSet.getString(1));
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return tags;
    }
	
	public static String getPath(String repo) {
		
		String path = null;
		query = "select folderPath from bugCollection where name = \""+repo+"\""; 
		ResultSet resultSet=fetchDB(query);
        try {
			while(resultSet.next())
				path=resultSet.getString(1);
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			  
		
		return path;
	}
	
	public static JPanel getsearchPanel(String string, String database, String host,String passwd, JTree jTree) {
		
		String query=null;
		ResultSet resultSet=null;
				
		ArrayList<Integer> categoryIdList= new ArrayList<Integer>();
		ArrayList<Integer> bugIdList= new ArrayList<Integer>();
		ArrayList<String> nameList= new ArrayList<String>();
		
		JPanel jPanel = new JPanel();
		GridLayout layout = new GridLayout(0,3);
		
		try{  	
			query = "select ID from bugCatagories where name = \""+string+"\"";
			resultSet=fetchDB(query);
	        while(resultSet.next())
				categoryIdList.add(resultSet.getInt(1)); 
	        			
			for(int id:categoryIdList){
				query = "select Bug_id from bugRecord where Cate_id = \""+id+"\"";
				resultSet=fetchDB(query);
		        while(resultSet.next())
					bugIdList.add(resultSet.getInt("Bug_id"));
			}
			
			for(int id:bugIdList){
				query = "select name from bugCollection where ID = \""+id+"\"";
				resultSet=fetchDB(query);
		        while(resultSet.next())
					nameList.add(resultSet.getString(1));
			}
			
			connection.close(); 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){ 
			System.out.println(e);
		}  
		
		layout.setHgap(10);
	    layout.setVgap(10);
		
		jPanel.setLayout(layout);
		jPanel.setPreferredSize(new Dimension(400, 200));
		for(String str: nameList) {
			JTextArea jTextArea = new JTextArea(1, 12);
			jTextArea.setFont(new Font("Courier",Font.PLAIN,(int) width/100));
			jTextArea.setEditable(false);
			jTextArea.setText(str);
			jTextArea.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						renderer.setQuery(jTextArea.getText());
						jTree.repaint();
					}
						
				}
			});
			jPanel.add(jTextArea);
		}
	    
		return jPanel;
	}
	
	public static ArrayList<String>  getSearchTags(String string, String database, String host,String passwd) {
		
		String query=null;
		ResultSet resultSet=null;
		
		int categoryId=0;
		
		ArrayList<Integer> bugIdList= new ArrayList<Integer>();
		ArrayList<String> nameList= new ArrayList<String>();
		
		try{  	
			query = "select ID from bugCatagories where name = \""+string+"\"";
			resultSet=fetchDB(query);
	        while(resultSet.next())
				categoryId=resultSet.getInt(1); 
	        
			query = "select Bug_id from bugRecord where Cate_id = \""+categoryId+"\"";
			resultSet=fetchDB(query);
	        while(resultSet.next())  
				bugIdList.add(resultSet.getInt("Bug_id"));
						
			for(int id:bugIdList){
				query = "select name from bugCollection where ID = \""+id+"\"";
				resultSet=fetchDB(query);
		        while(resultSet.next())
					nameList.add(resultSet.getString(1));
			}
			
			connection.close();  
		} catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){ 
			System.out.println(e);
		} 		
		
	    return nameList;
	}
	
	static void modifyFrame(String path,JPanel jPanel1, JTextField jTextField, JPanel jPanel2, JFrame frame, String database, String host,String passwd) {
		
		JPanel upperPanel = new JPanel();
		JPanel lowerPanel = new JPanel();
		
		// Get a fixed file
	    JTextArea tareaFixed = new JTextArea(20, 40);
	    
	    // Get a Buggy file
	    JTextArea tareaBuggy = new JTextArea(20, 40);
		
	    // Get a explanation file
	    JTextArea tareaReadMe = new JTextArea(5,50);
		
	    JScrollPane topleft = null;
	    JScrollPane topright = null; 
	    JScrollPane bottom = null;
	    
	    File file= null, file1= null, file2= null;
	    BufferedReader input = null;
	    
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
	    tareaBuggy.setFont(new Font("Courier",Font.PLAIN,(int) width/75));
	    tareaBuggy.setEditable(false);
	    topleft = new JScrollPane(tareaBuggy);
	    topleft.getVerticalScrollBar().setPreferredSize(new Dimension(30, 0));
	    topleft.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 30));
	    
	    tareaFixed.setFont(new Font("Courier",Font.PLAIN,(int) width/75));
	    tareaFixed.setEditable(false);
	    topright = new JScrollPane(tareaFixed);
	    topright.getVerticalScrollBar().setPreferredSize(new Dimension(30, 0));
	    topright.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 30));
	    
	    tareaReadMe.setFont(new Font("Arial",Font.PLAIN,(int) width/75));
	    tareaReadMe.setEditable(false);
	    bottom = new JScrollPane(tareaReadMe);
	    bottom.getVerticalScrollBar().setPreferredSize(new Dimension(30, 0));
	    bottom.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 30));
	    
	    upperPanel.add(topleft);
	    upperPanel.add(topright);
	    lowerPanel.add(bottom);
	    
	    jPanel2.removeAll();
		jPanel2.add(upperPanel);
		jPanel2.add(lowerPanel);
			
		try {
			File file0 = new File(path);
			for(File file3: file0.listFiles()){
				
				if (file3.getName().startsWith("fixed_version.")) {
					//file = new File(file3.getAbsolutePath().toString());
					file = file3;
				}else if(file3.getName().startsWith("buggy_version."))
					file1 = file3;
			}
			//file = new File(path + "/fixed_version.c");
		    //file1 = new File(path + "/buggy_version.c");
		    file2 = new File(path + "/Explanation.txt");
		}catch (Exception e) {
		    //file = new File(path + "/fixed_version.cu");
		    //file1 = new File(path + "/buggy_version.cu");
			e.printStackTrace();
		}
		
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		    tareaFixed.read(input, "READING FIXED FILE :-)");
		} catch(FileNotFoundException fnfe){
			tareaFixed.setText("The File is Missing!!!");
		} catch (Exception e){
		    e.printStackTrace();
		}
		
		try{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
		    tareaBuggy.read(input, "READING BUGGY FILE :-)");
	    } catch(FileNotFoundException fnfe){
			tareaFixed.setText("The File is Missing!!!");
		} catch (Exception e){
		    e.printStackTrace();
		}
		    
		try{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
			tareaReadMe.read(input, "READING FILE :-)");
		} catch(FileNotFoundException fnfe){
			tareaFixed.setText("The File is Missing!!!");
		} catch (Exception e){
			e.printStackTrace();
		}
		    
		frame.getContentPane().add(jPanel2);
		frame.pack();
	}
	
	public static ArrayList<DefaultMutableTreeNode> getLeafNodes(DefaultMutableTreeNode root) {
	    ArrayList<DefaultMutableTreeNode> leafs = new ArrayList<>();
	    _getLeafNodes(root, leafs);
	    return leafs;
	}
	
	private static void _getLeafNodes(DefaultMutableTreeNode parent, ArrayList<DefaultMutableTreeNode> leafs) {
	    Enumeration<TreeNode> children = parent.children();
	    while (children.hasMoreElements()) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)children.nextElement();
	        if (!node.isLeaf()) {
	            leafs.add(node);
	            _getLeafNodes(node, leafs);
	        }
	    }
	}
	
	
	/**
	 * Create the application.
	 */
	public BaseView_v4() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.getWidth();
		height = screenSize.getHeight();
		frame.setBounds(0, 0, (int) width, (int) height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
