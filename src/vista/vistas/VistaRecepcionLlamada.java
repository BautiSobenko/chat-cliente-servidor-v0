package vista.vistas;

import vista.interfaces.IVistaRecepcionLlamada;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;

public class VistaRecepcionLlamada extends JFrame implements IVistaRecepcionLlamada {

	private JPanel contentPane;
	private JButton btnAceptar;
	private JButton btnRechazar;
	private JLabel lblIP;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaRecepcionLlamada frame = new VistaRecepcionLlamada();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VistaRecepcionLlamada() {
		setTitle("Llamada Entrante");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 625, 264);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnAceptar = new JButton("Aceptar");
		btnAceptar.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnAceptar.setBounds(376, 131, 139, 54);
		contentPane.add(btnAceptar);
		
		btnRechazar = new JButton("Rechazar");
		btnRechazar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRechazar.setBounds(95, 131, 139, 54);
		contentPane.add(btnRechazar);
		
		lblIP = new JLabel("");
		lblIP.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblIP.setBounds(76, 25, 466, 36);
		contentPane.add(lblIP);
		
		JLabel lblNewLabel_1 = new JLabel("\u00BFDesea conectar?");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel_1.setBounds(234, 81, 147, 14);
		contentPane.add(lblNewLabel_1);
	}



	@Override
	public void setActionListener(ActionListener controlador) {
		this.btnAceptar.addActionListener(controlador);
		this.btnRechazar.addActionListener(controlador);
	}

	@Override
	public void mostrar() {
		this.setVisible(true);
	}

	@Override
	public void esconder() {
		this.setVisible(false);
	}

	@Override
	public void setLabelIP(String IP) {
		this.lblIP.setText("Llamada entrante de la direccion:  "+ IP);
	}
}
