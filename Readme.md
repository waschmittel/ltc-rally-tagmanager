# LTC Sponsor Rally Tag Manager and Lap Counter

Desktop Java Application to

- count laps with NFC Smartcard reader and tags
- manage those tag's assignments to runner numbers

Tested with ACR122U readers.

## Linux

On Ubuntu and derivatives, follow the instructions from 

https://www.reddit.com/r/nfctools/comments/11l8s00/acr122u_in_linux/

which refers to

https://www.jamesridgway.co.uk/install-acr122u-drivers-on-linux-mint-and-kubuntu/

In case the thread disappears, here are the basics:

1. Blacklist the pn533 and nfc modules: `printf 'blacklist pn533\nblacklist pn533_usb\nblacklist nfc\n' | sudo tee /etc/modprobe.d/blacklist-pn533.conf`
2. `sudo apt install libacsccid1 pcsc-tools pcscd`
3. Ensure the pscsd.socket is enabled: `sudo systemctl enable --now pcscd.socket`


