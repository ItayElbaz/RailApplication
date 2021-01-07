# Israel Rail Vouchers Application

Israel Rail Vouchers app designed to address the need of automatic vouchers issuance for daily/weekly Israel Rail passengers during COVID-19 new restrictions.
Every passanger must obtain a train entry pass in advance (AKA Voucher) in order enter the train station. Eash voucher is for specific train.
Israil Rail website require from the passangers to book manualy each voucher for every train route -
our app solve this by allowing the users to schedule train routes as simple as scheduling a repeated alarm clock!
More details can be found in the user manual.

The app currently working with our temporary server, all you need to do is simply install `IsraelRailVouchers.apk` in your android device.

For those who desire to install their own setup here's the installation details:

### How to install the server?

#### Prerequisites
* Ubuntu 18.04 server with http traffic enabled (port 80)
* Python3 installed


#### Installation

    sudo apt-get update
    sudo apt-get install python-pip3
    sudo pip3 install flask
    sudo apt-get install apache2
    sudo apt-get install libapache2-mod-wsgi-py3
    sudo service apache2 restart

#### Deployment

    mkdir ~/railserver
    sudo ln -sT ~/railserver /var/www/html/railserver
    
Create a basic HTML page

    cd ~/railserver
    mkdir uploads
    echo "Hello World" > index.html

You should now see "Hello World" displayed if you navigate to (your instance public DNS)/railserver in your browser.

Copy the server.py file to the created folder (`mv server.py ~/railserver/`)

Put the following content in a file named `railserver.wsgi`:
```python
import sys
sys.path.insert(0, '/var/www/html/railserver')

from server import app as application
```

Edit `/etc/apache2/sites-enabled/000-default.conf`

```python
#WSGIDaemonProcess server threads=5
WSGIScriptAlias / /var/www/html/railserver/railserver.wsgi

<Directory railserver>
    WSGIProcessGroup railserver
    WSGIApplicationGroup %{GLOBAL}
    Order deny,allow
    Allow from all
</Directory>
```

Restart the server

    sudo apachectl restart

Now your server is ready to go!

Note: currently the application is sending the data to specific public DNS of our tests server, please pull this repository and change it in order to work with the server (under `PostDataToServer.java`)
Once changed, create an APK using android studio.


