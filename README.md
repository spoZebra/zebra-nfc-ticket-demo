# Zebra NFC Ticket Demo
## Description
Simple PoC developed to demonstrate Zebra mobile computer's capability of reading non-payment related items (Passes) via NFC directly from Google and Apple digital wallets.
Passes are not limited to Event tickets only but Boarding passes, Loyalty cards, Ski tickets, Mobile Driver licenses, etc.

Zebra VAS SDK allows our customers to read any pass from a digital Wallet out of the box quickly: there is no need to implement Smart Tap and Apple VAS protocols as our VAS Service takes care of everything!

### Official Resources
- Registration of my presentation at DevCon 2023 in Madrid: [Link to video](https://www.zebra.com/content/dam/zebra_dam/en/video/web-production/zebra-devcon2023-video-website-emc-introducing-the-new-zebra-apple-nfc-vas-sdk-simone-pozzobon-en-us.mp4)
- And related slides: [Link to slides](https://www.zebra.com/content/dam/zebra_dam/en/presentation/customer-facing/zebra-devcon2023-presentation-customer-facing-introducing-the-new-zebra-apple-nfc-simone-pozzobon-en-us.pdf)
- Zebra Overview page: [Zebra - Digital Wallet](https://www.zebra.com/us/en/software/mobile-computer-software/mobile-wallets.html)
- Full Technical documentation: [Zebra TechDocs - VAS SDK](https://techdocs.zebra.com/nfc-vas/2-0/guide/about/)
  
## NFC Pass Flow
![image](https://github.com/spoZebra/zebra-nfc-ticket-demo/assets/101400857/8e7b94f4-6bcb-4fe7-86fc-f9deb0e2af27)

## Requirements
- A Zebra mobile computer VAS certified (e.g. TC22/27, TC53/58, ET40/45 etc). [Full list](https://www.zebra.com/us/en/support-downloads/software/developer-tools/value-added-services-sdk.html)
- Any iOS or Android device (including Watches) with Apple or Google Wallet containing the pass to be read.

Note: This sample does not include pass generation. Please use this demo app if you can't generate your own pass: [Full Demo](https://techdocs.zebra.com/nfc-vas/2-0/guide/demo/)

## Usage
- Download and install VAS SDK from [here](https://www.zebra.com/us/en/support-downloads/software/developer-tools/value-added-services-sdk.html)
- Install VAS Service APK on your device
- Add and fill the following variable with your data inside your *local.properties* project file:
```
APPLEVAS_PRIVATE_KEY="-----BEGIN EC PRIVATE KEY----- Your Apple private key here"
GOOGLESMARTAPP_PRIVATE_KEY="-----BEGIN EC PRIVATE KEY----- Your Google private key here"
GOOGLESMARTAPP_KEY_VERSION="Your Google private key version"
GOOGLESMARTAPP_COLLECTOR_ID="Youre Google Collector ID"
```
- Run the app and read your pass :)



