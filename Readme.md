# Tready Project
Welcome to the Tready Project which is an Open Source security system developed by the Mobile Privacy and Security Team at Baldwin Wallace. If you have questions about the project, please contact a member of the research group. Below are guides for setting up Pi Eyes and the Pi Hub using what is available in this repository.

# Setting Up Pi Eye
_These steps assume you are starting from a fresh install_
1. Run raspberry pi config and do the following:
	1. (Optional) Set Hostname
		1. Reboot
	2. Enable Camera
	3. Enable SSH
		1. Reboot
	4. Set Keyboard
	5. Change password of pi user
	6. Set locale for both system and WiFi
2. Configure Wifi
	1. Run `wpa_passphrase ssid_name psk_pass`
	2. Copy output into wpa_supplicant
	3. Ensure you can connect by enabling/disabling wifi
3. Reboot
4. Test camera by doing the following on the command line
	1. `raspistill -rot 180 -t 500000` and then open up t.jpg
	2. Adjust focus as necessary and repeat if necessary
	3. If good, press `ctrl+c`
5. Setup Tready
	1. SCP files or pull from bitbucket
	2. Modify config with name of pi eye and setup URL, username, password
	3. Test run ready script and wave hand in front of camera, confirm file upload
6. Setup autorun
	1. Edit `/etc/rc.local` and add the following line before exit0:
```
/home/pi/treadyEye.py & >/home/pi/treadyEyeError.log 2>&1
/home/pi/treadyEye-Uploader.py & >/home/pi/treadyEye-UploadError.log 2>&1

# The below two lines are optional for turning off LEDs. This is helpful to not have false alerts with motion caused by light reflection and it also does not make the Pis placement more noticable.
echo 0 | sudo tee /sys/class/leds/led0/brightness
echo 0 | sudo tee /sys/class/leds/led1/brightness
```
8. Reboot and Test and you are ready to go!
9. If using Pi Model 3, you may want to turn off the LEDs: _You may want to do this so you don’t see reflection_
```shell
root@pieye2:~# echo 0 >/sys/class/leds/led0/brightness
root@pieye2:~# echo 0 >/sys/class/leds/led1/brightness
```
10. Setup cron job in root's cron to check for treadyProcess and ensure if they die that they are restarted
`*/1 *  *   *   *    /home/pi/treadyManager.sh`


# Setting Up Pi Hub
_This guide assumes you are doing a setup from a fresh install_
1. Run raspberry pi config and do the following:
	1. (Optional) Set Hostname
		1. Reboot
	2. Enable SSH
	3. Set Locale, Timezone, Keyboard, Wifi Country
		1. Time may be out of sync until you set WiFi (see below)
	4. Set Keyboard
	5. Change password of pi user
2. Configure Wifi
	1. Run `wpa_passphrase ssid_name psk_pass`
	2. Copy output into wpa_supplicant
	3. Ensure you can connect by enabling/disabling wifi or rebooting
3. Reboot

## Setup Lamp
1. Setup LAMP
	1. `sudo apt-get update`
	2. Install Apache: `sudo apt-get install apache2`
	3. Install MySQL: `sudo apt-get install mysql-server`
	4. Install PHP: `sudo apt-get install php`
	5. Install PHP MySQL Package `sudo apt-get install php-mysql`
	6. Restart Apache `service apache2 restart`
	7. Install MP4 Converter `sudo apt-get install gpac`
	8. Install FFMPEG `sudo apt-get install ffmpeg`

2. Setup MySQL
```
sudo mysql -u root
create database skunkworks
create user 'skunkworks'@'localhost' IDENTIFIED BY 'ENTER_PASS_HERE'
grant all privileges on *.* to 'skunkworks'@'localhost' with GRANT OPTION
flush privileges
```

Then create database use the below SQL
```
use skunkworks;
CREATE TABLE `tblDevices` (
  `deviceIdentifier` bigint(20) DEFAULT NULL,
  `ipAddress` varchar(75) DEFAULT NULL,
  `deviceEnabled` enum('Yes','No') DEFAULT 'Yes',
  `lastContact` int(11) DEFAULT NULL,
  `deviceName` varchar(75) DEFAULT NULL,
  `preMotionCapture` int(11) NOT NULL DEFAULT '3',
  `postMotionCapture` int(11) NOT NULL DEFAULT '3',
  `captureResolution` enum('0','1','2','3','4') NOT NULL DEFAULT '2',
  `motionDetectionMagnitudeThreshold` int(11) NOT NULL DEFAULT '60',
  `motionDetectionVectorThreshold` int(11) NOT NULL DEFAULT '10',
  `orientation` enum('landscapePowerUp','landscapePowerDown','portraitUpsideDown','portrait') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `tblEvents` (
  `eventId` int(11) NOT NULL,
  `deviceIdentifier` text,
  `timeStarted` int(11) DEFAULT NULL,
  `timeEnded` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `tblNotificationsReceived` (
  `eventSummaryId` int(11) NOT NULL,
  `userId` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `tblUsers` (
  `userId` int(11) NOT NULL,
  `username` varchar(75) DEFAULT NULL,
  `password` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `tblEvents`
  ADD PRIMARY KEY (`eventId`);

ALTER TABLE `tblUsers`
  ADD PRIMARY KEY (`userId`);


ALTER TABLE `tblEvents`
  MODIFY `eventId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15950;

ALTER TABLE `tblUsers`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT;

```
3. Setup PHP to allow videos with max sizes:
``` sh 
php_value upload_max_filesize 50M
php_value post_max_size 50M
# Change memory limit to be big enough to get video and then process video
memory_limit = 512M
```

## Setup Tready Server
1. SCP files or pull from bitbucket: `git clone https://bkrupp@bitbucket.org/bkrupp/skunkworks.git`
	1. Move files over to `/var/www/html`  : `sudo cp -r ~/skunkworks/webservices/* /var/www/html`
2. Make directory for files:
	1. `/var/www $ sudo mkdir eventImages`
	2. `chmod a+w eventImages`
3. Update mysql password in model/db.php
4. Create user account:
	1. Go to client page and enter password in hash
	2. Then go to mysql command line and insert username and hash: `insert into tblUsers (username, password) values ("youruser", "somelonghash")`
	3. **Remember this username and password as you will have to install it on the Pi Eyes**

## Setting Up SSL
1. Enable SSL
`sudo a2enmod ssl`
2. Create 2 Self Signed Certs
```sh
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 3650 -subj "/C=US/ST=Ohio/L=Cleveland/O=Krupp/OU=Security/CN=10.0.1.50"

openssl req -x509 -newkey rsa:4096 -keyout ext_key.pem -out ext_cert.pem -days 3650 -subj "/C=US/ST=Ohio/L=Cleveland/O=Krupp/OU=Security/CN=96.27.122.128"

# Remove passphrase for each key
openssl rsa -in key.pem -out localkey.pem
```
3. Copy both keys and certs into a secure directory and then configure apache to point to them `/etc/apache2/sites-enabled/000-default.conf`: You should have two VirtualHost, one for internal IP and one for external
```
<VirtualHost x.x.x.x:443>
        SSLEngine on
        SSLCertificateFile /etc/apache2/ssl/local_cert.pem
        SSLCertificateKeyFile /etc/apache2/ssl/localkey.pem
```
5. Restart apache, and make sure it works
6. Copy both certs to the web root and access it from mobile browser to import the certs so that you can establish trust. For iOS, you want to install them as configuration profiles (the browser will prompt you you) then go to Settings -> General -> About -> Certificate Trust Settings and then Enable both IPs that you imported

## Setting Up Apache htaccess
Create a .htaccess file in `/var/www/html/skunkworks/eventImages` with the following contents:
```
Authtype Basic
AuthName "Username and password required"
AuthUserFile /var/www/treadyHtaccessAuth
Require valid-user
```

Add the following to your default configuration file: `/etc/apache2/sites-enabled/000-default.conf`
``` 
<Directory "/var/www">
AllowOverride All
</Directory>
```

Then create the username file:
`sudo htpasswd -c /var/www/treadyHtaccessAuth yourusernamefortready`

