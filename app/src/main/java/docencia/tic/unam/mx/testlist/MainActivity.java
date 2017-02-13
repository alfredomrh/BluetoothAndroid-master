package docencia.tic.unam.mx.testlist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //constante que devuelve el valor de activacion del bluetooth
    public static final int  REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se consigue el adaptador bt local del dispositivo

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //si el objeto resulta null es que el disp. no tiene bluetooth

        if (mBluetoothAdapter == null) {

            msj("No hay capacidad de Bluetooth.");

        } else { //si tiene bt, vemos si esta deshabilitado
            if (!mBluetoothAdapter.isEnabled()) {

                //si lo está, lanzamos un intent para habilitarlo aunque podriamos hacerlo sin
                //requerirselo al usuario

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else { //si está presente y habilitado

                this.setupBluetooth( ); //llamamos al metodo de configuración
            }
        }
    }

    /**
     * metodo que recoge el resultado de la intencion que lanzamos para activar el bt
     * si requestCode y resultCode es 1, se lanza el metodo de la configuración de bluetooth
     * @param requestCode codigo de solicitud
     * @param resultCode codigo resultado
     * @param data datos de la intención
     */
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
            if( requestCode == REQUEST_ENABLE_BT && resultCode ==  RESULT_OK ) {
            this.setupBluetooth( );
        }
    }

    /**
     * Metodo que vuelve a capturar el adaptador del dispositivo y si está habilitado
     * guarda en un array los dispositivos vinculados si los hay. Despues los muestra en un listView
     * y si haces click sobre alguno de ellos, lanza la activity ControlPanel, pasandole la
     * mac del dispositivo seleccionado.
     */

    private void setupBluetooth( ) {
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            // Listar los dispositivos emparejados
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                BluetoothDevice[] devices = new BluetoothDevice[pairedDevices.size()];
                // Loop through paired devices
                int i = 0;
                for (BluetoothDevice device : pairedDevices) {
                    devices[ i++ ] = device;
                }

                final ListView listview = (ListView) findViewById(R.id.listview);

                listview.setAdapter( new MyBluetoothAdapter(getApplicationContext(), devices ) );

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition( position );
                        BluetoothDevice actual = mBluetoothAdapter.getRemoteDevice( device.getAddress() );

                        mBluetoothAdapter.cancelDiscovery();

                        Intent i = new Intent(MainActivity.this, ControlPanel.class);
                        i.putExtra("address", actual.getAddress());
                        startActivity( i );
                    }
                });
            }

        }
    }

    /**
     * metodo para sacar un mensaje en pantalla
     * @param mensaje
     */

    public void msj(String mensaje) {
        try {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        } catch (Exception e ) {
            Log.e("ERROR:", e.getMessage());
        }
    }
}
