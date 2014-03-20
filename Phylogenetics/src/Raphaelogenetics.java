import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class Raphaelogenetics extends JFrame {

	private JPanel contentPane;
	private File[] infiles;
	private ArrayList<Organism<Nucleotide>> organisms;
	private PhyloTree t = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					Raphaelogenetics frame = new Raphaelogenetics();
					frame.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Raphaelogenetics() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mnFile_itmOpen = new JMenuItem("Open");
		mnFile_itmOpen.setAccelerator(KeyStroke
				.getKeyStroke(KeyEvent.VK_O, InputEvent.META_MASK));
		mnFile_itmOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		
				//Set up file dialog
				FileDialog fd = new FileDialog(Raphaelogenetics.this, "Choose Input Data:", FileDialog.LOAD);
				fd.setMultipleMode(true);
				fd.setDirectory("~");
				fd.setVisible(true);
		
				infiles = fd.getFiles();
				try {
					organisms = Phylogenetics.readMultipleFASTAs(infiles);
				}
				catch (Exception ex) {
					JOptionPane
							.showMessageDialog(null, "Files could not be read!");
					ex.printStackTrace();
				}
			}
		});
		mnFile.add(mnFile_itmOpen);

		JMenuItem mnFile_itmExport = new JMenuItem("Export");
		mnFile_itmExport.setAccelerator(KeyStroke
				.getKeyStroke(KeyEvent.VK_E, InputEvent.META_MASK));
		mnFile.add(mnFile_itmExport);

		JMenu mnTree = new JMenu("Tree");
		menuBar.add(mnTree);

		JRadioButtonMenuItem rdbtnmntmView = new JRadioButtonMenuItem("View1");
		mnTree.add(rdbtnmntmView);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{440, 0};
		gbl_contentPane.rowHeights = new int[]{123, 123, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
				gbc_tabbedPane.weightx = 1.0;
				gbc_tabbedPane.weighty = 1.0;
				gbc_tabbedPane.fill = GridBagConstraints.BOTH;
				gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
				gbc_tabbedPane.gridx = 0;
				gbc_tabbedPane.gridy = 0;
				contentPane.add(tabbedPane, gbc_tabbedPane);
				
						JPanel treeview = new JPanel();
						tabbedPane.addTab("Tree", null, treeview, null);
						
								JPanel dataview = new JPanel();
								tabbedPane.addTab("Data", null, dataview, null);
		
		JLabel lblNewLabel = new JLabel("New label", JLabel.LEFT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.weighty = 0.1;
		gbc_lblNewLabel.anchor = GridBagConstraints.SOUTH;
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
	}

}
