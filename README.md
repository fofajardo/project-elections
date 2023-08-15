# Halal

**Halal** aka "Project Elections" is a voting or polling system written in Java/Jakarta EE. This is primarily patterned against the Philippine Elections, providing options to vote for a configurable set of candidates, positions, political parties, and party lists.

The documentation is mostly complete, with comments for every class, with the exception of the classes under `elections.models`. The database schema and sample data are *not* included and may come in a future update.

## Features
- Completely configurable: everything from the positions to the candidates are configurable and not hardcoded<sup>[1]</sup> into the software.
- Accounts: voters and administrators are all stored in the same table, with configurable roles<sup>[2]</sup>, uses hashed passwords (not stored in plain-text), signing in via QR code.
- MVC: each part of the software is separated neatly, following the model-view-controller architecture.
- Responsive design using Bootstrap 5.
- Viewable candidate listing, ballot receipts, and real-time results with graphs and vote count.

[1] Except for the name of the ballot.

[2] This feature was not completely implemented and was turned into a simple voter vs. administrator role only. Likewise, creating and modifying candidates, positions, political parties, and party lists are not possible through the web interface and must be modified manually in the database.

## License

Copyright © 2022 Francis Dominic Fajardo

This software is licensed under the MPL version 2.0. For more information, read this repository's LICENSE. See the AUTHORS file for details about this software's developers and relevant contact details.

---

The following directories and files are covered by these licenses:
- src/main/webapp/assets/bs [MIT License: Copyright 2011-2021 The Bootstrap Authors, Copyright 2011-2021 Twitter, Inc.] ([Repository](https://github.com/twbs/bootstrap/))
- src/main/webapp/assets/qrcode.min.js [MIT License: Copyright 2012 davidshimjs] ([Repository](https://github.com/davidshimjs/qrcodejs/))
- src/main/webapp/assets/qrcode-scan.min [Apache License 2.0] ([Repository](https://github.com/mebjas/html5-qrcode))
- src/main/webapp/assets/images [unDraw License: Copyright 2022 Katerina Limpitsouni] ([Official Site](https://undraw.co/))
