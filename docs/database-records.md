# First Down database records

This reference shows the 181 records in the supplied SQL inserts, grouped by table. It reflects the supplied script rather than a live database query. Passwords, card numbers, security codes, phone numbers, and email addresses are omitted. A dash (—) represents `NULL`.

## Tables

- [CUSTOMER](#customer) (20 rows)
- [ACCOUNT](#account) (20 rows)
- [PAYMENTCARD](#paymentcard) (20 rows)
- [LOCATION](#location) (8 rows)
- [INVENTORYITEM](#inventoryitem) (30 rows)
- [MENUITEM](#menuitem) (15 rows)
- [ORDERS](#orders) (17 rows)
- [ONLINEORDER](#onlineorder) (8 rows)
- [INPERSONORDER](#inpersonorder) (9 rows)
- [ORDERITEM](#orderitem) (34 rows)

## CUSTOMER

| CUSTOMER_ID | CUSTOMER_NAME |
| --- | --- |
| 1 | Patrick Mahomes |
| 2 | Joe Burrow |
| 3 | Josh Allen |
| 4 | Jalen Hurts |
| 5 | Lamar Jackson |
| 6 | Justin Jefferson |
| 7 | JaMarr Chase |
| 8 | CeeDee Lamb |
| 9 | Tyreek Hill |
| 10 | Amon-Ra St. Brown |
| 11 | Garrett Wilson |
| 12 | Puka Nacua |
| 13 | Christian McCaffrey |
| 14 | Bijan Robinson |
| 15 | Micah Parsons |
| 16 | Nick Bosa |
| 17 | Jalen Coker |
| 18 | Trevor Lawrence |
| 19 | Devon Achane |
| 20 | Jordan Love |

## ACCOUNT

| ACCOUNT_ID | USERNAME | POINTS | CUSTOMER_ID |
| --- | --- | --- | --- |
| 1 | pmh_x29 | 120 | 1 |
| 2 | burrow_q3 | 90 | 2 |
| 3 | allen_jet7 | 110 | 3 |
| 4 | hurtsZ12 | 105 | 4 |
| 5 | ljax_r8 | 130 | 5 |
| 6 | jjeff_gg9 | 95 | 6 |
| 7 | chase_r5 | 85 | 7 |
| 8 | cdl_srg | 115 | 8 |
| 9 | tyreek_14 | 125 | 9 |
| 10 | amonra_55 | 88 | 10 |
| 11 | gwilsn_x | 92 | 11 |
| 12 | pnacua8 | 97 | 12 |
| 13 | cmc_23run | 140 | 13 |
| 14 | bijan_atx | 80 | 14 |
| 15 | mparsons11 | 135 | 15 |
| 16 | jcoker42 | 100 | 16 |
| 17 | cjstroud_qb | 108 | 17 |
| 18 | tlaw_duv | 102 | 18 |
| 19 | devstacks | 93 | 19 |
| 20 | jlove_gb10 | 89 | 20 |

## PAYMENTCARD

| CARD_ID | EXPIRATION_DATE | CARD_TYPE | CUSTOMER_ID |
| --- | --- | --- | --- |
| 1 | 2027-12-31 | VISA | 1 |
| 2 | 2026-11-30 | MC | 2 |
| 3 | 2028-10-31 | VISA | 3 |
| 4 | 2026-09-30 | AMEX | 4 |
| 5 | 2027-08-31 | MC | 5 |
| 6 | 2026-07-31 | VISA | 6 |
| 7 | 2028-06-30 | DISC | 7 |
| 8 | 2027-05-31 | MC | 8 |
| 9 | 2026-04-30 | VISA | 9 |
| 10 | 2028-03-31 | VISA | 10 |
| 11 | 2027-02-28 | MC | 11 |
| 12 | 2026-01-31 | DISC | 12 |
| 13 | 2028-12-31 | AMEX | 13 |
| 14 | 2027-11-30 | MC | 14 |
| 15 | 2026-10-31 | VISA | 15 |
| 16 | 2028-09-30 | DISC | 16 |
| 17 | 2027-08-31 | MC | 17 |
| 18 | 2026-07-31 | VISA | 18 |
| 19 | 2028-06-30 | VISA | 19 |
| 20 | 2027-05-31 | MC | 20 |

## LOCATION

| LOCATION_ID | ADDRESS | CITY_NAME | TAX_RATE |
| --- | --- | --- | --- |
| 1 | 1 Arrowhead Dr | Kansas City | 8.60 |
| 2 | 1265 Lombardi Ave | Green Bay | 5.50 |
| 3 | 1001 S Figueroa St | Los Angeles | 9.50 |
| 4 | 1020 Pattison Ave | Philadelphia | 8.00 |
| 5 | 1 ATnT Way | Arlington | 8.25 |
| 6 | 347 Don Shula Dr | Miami | 7.00 |
| 7 | 2000 Brush St | Detroit | 6.00 |
| 8 | 1 MetLife Stadium Dr | East Rutherford | 8.88 |

## INVENTORYITEM

| INVENTORY_ID | INVENTORY_NAME | ITEM_COST |
| --- | --- | --- |
| 1 | Brioche Bun | 0.40 |
| 2 | Beef Patty | 1.60 |
| 3 | Cheddar Slice | 0.30 |
| 4 | Lettuce | 0.10 |
| 5 | Tomato | 0.15 |
| 6 | Onion | 0.08 |
| 7 | Pickles | 0.07 |
| 8 | Bacon Strip | 0.50 |
| 9 | Chicken Thigh | 1.20 |
| 10 | Buffalo Sauce | 0.25 |
| 11 | Ranch | 0.20 |
| 12 | Blue Cheese Crumble | 0.35 |
| 13 | Tortilla Chips | 0.70 |
| 14 | Queso | 0.60 |
| 15 | Pico de Gallo | 0.25 |
| 16 | Sour Cream | 0.22 |
| 17 | Jalapenos | 0.12 |
| 18 | Hot Dog Sausage | 0.90 |
| 19 | Hot Dog Bun | 0.30 |
| 20 | Bratwurst | 1.10 |
| 21 | Pretzel Bun | 0.55 |
| 22 | Mustard | 0.05 |
| 23 | Ketchup | 0.05 |
| 24 | Fries (6oz) | 0.65 |
| 25 | Chili | 0.75 |
| 26 | Mac Pasta | 0.35 |
| 27 | Cheese Sauce | 0.45 |
| 28 | Pork Ribs (quarter) | 2.80 |
| 29 | Coleslaw | 0.40 |
| 30 | Vanilla Shake Base | 0.90 |

## MENUITEM

| MENU_ITEM_ID | ITEM_NAME | BASE_PRICE | ITEM_CATEGORY |
| --- | --- | --- | --- |
| 1 | Blitz Burger | 12.99 | BURGER |
| 2 | Goal Line PoBoy | 11.49 | SANDWICH |
| 3 | Two-Minute Wings | 13.49 | WINGS |
| 4 | Audible Cheesesteak | 12.25 | SANDWICH |
| 5 | Option Route Wrap | 10.75 | WRAP |
| 6 | Red Zone Jambalaya | 13.99 | BOWL |
| 7 | Pick-Six Chili Dog | 9.25 | DOG |
| 8 | End Zone Tacos | 11.10 | TACO |
| 9 | Hail Mary Nachos | 12.10 | NACHO |
| 10 | Sideline Salad | 10.20 | SALAD |
| 11 | Power Sweep Protein Bowl | 12.80 | BOWL |
| 12 | Goalpost Ribs | 16.90 | BBQ |
| 13 | Snap Count Brat | 11.60 | BRAT |
| 14 | Play-Action Slider | 9.90 | SLIDER |
| 15 | Two-Point Shake | 6.50 | DESSERT |

## ORDERS

| ORDER_ID | ORDER_TIMESTAMP | PICKUP_TS | LOCATION_ID | CUSTOMER_ID | ACCOUNT_ID | CARD_ID |
| --- | --- | --- | --- | --- | --- | --- |
| 101 | 2026-03-15 11:42:00 | — | 1 | 1 | 1 | 1 |
| 102 | 2026-03-17 18:08:00 | 2026-03-17 18:35:00 | 2 | 2 | 2 | 2 |
| 103 | 2026-03-20 12:26:00 | — | 3 | 3 | 3 | 3 |
| 104 | 2026-03-23 19:14:00 | 2026-03-23 19:50:00 | 4 | 4 | 4 | 4 |
| 105 | 2026-03-27 13:03:00 | — | 5 | 5 | 5 | 5 |
| 106 | 2026-03-30 17:31:00 | 2026-03-30 18:05:00 | 6 | 6 | 6 | 6 |
| 107 | 2026-04-02 11:18:00 | — | 7 | 7 | 7 | 7 |
| 108 | 2026-04-05 20:07:00 | 2026-04-05 20:40:00 | 8 | 8 | 8 | 8 |
| 109 | 2026-04-08 12:54:00 | — | 1 | 9 | 9 | 9 |
| 110 | 2026-04-11 18:43:00 | 2026-04-11 19:10:00 | 2 | 10 | 10 | 10 |
| 111 | 2026-04-14 13:37:00 | — | 3 | 11 | 11 | 11 |
| 112 | 2026-04-18 17:56:00 | 2026-04-18 18:28:00 | 4 | 12 | 12 | 12 |
| 113 | 2026-04-21 11:47:00 | — | 5 | 13 | 13 | 13 |
| 114 | 2026-04-24 19:22:00 | 2026-04-24 19:55:00 | 6 | 14 | 14 | 14 |
| 115 | 2026-04-26 12:09:00 | — | 7 | 15 | 15 | 15 |
| 116 | 2026-04-29 18:16:00 | 2026-04-29 18:52:00 | 8 | 16 | 16 | 16 |
| 117 | 2026-05-01 13:28:00 | — | 1 | 17 | 17 | 17 |

## ONLINEORDER

| ORDER_ID | PICKUP_TIMESTAMP |
| --- | --- |
| 102 | 2026-03-17 18:35:00 |
| 104 | 2026-03-23 19:50:00 |
| 106 | 2026-03-30 18:05:00 |
| 108 | 2026-04-05 20:40:00 |
| 110 | 2026-04-11 19:10:00 |
| 112 | 2026-04-18 18:28:00 |
| 114 | 2026-04-24 19:55:00 |
| 116 | 2026-04-29 18:52:00 |

## INPERSONORDER

| ORDER_ID | TABLE_NUMBER |
| --- | --- |
| 101 | 11 |
| 103 | 7 |
| 105 | 14 |
| 107 | 5 |
| 109 | 9 |
| 111 | 6 |
| 113 | 12 |
| 115 | 4 |
| 117 | 10 |

## ORDERITEM

| ORDER_ITEM_ID | QUANTITY | UNIT_PRICE | TAX_AMOUNT | ORDER_ID | MENU_ITEM_ID |
| --- | --- | --- | --- | --- | --- |
| 1001 | 2 | 12.99 | 2.23 | 101 | 1 |
| 1002 | 1 | 9.25 | 0.80 | 101 | 7 |
| 1003 | 1 | 11.49 | 0.63 | 102 | 2 |
| 1004 | 1 | 6.20 | 0.34 | 102 | 15 |
| 1005 | 1 | 13.99 | 1.33 | 103 | 3 |
| 1006 | 2 | 10.80 | 2.05 | 103 | 8 |
| 1007 | 1 | 12.05 | 0.96 | 104 | 4 |
| 1008 | 1 | 10.05 | 0.80 | 104 | 10 |
| 1009 | 1 | 10.90 | 0.90 | 105 | 5 |
| 1010 | 1 | 17.00 | 1.40 | 105 | 12 |
| 1011 | 1 | 13.69 | 0.96 | 106 | 6 |
| 1012 | 1 | 9.05 | 0.63 | 106 | 7 |
| 1013 | 1 | 11.20 | 0.67 | 107 | 5 |
| 1014 | 1 | 11.95 | 0.72 | 107 | 4 |
| 1015 | 1 | 12.75 | 1.13 | 108 | 9 |
| 1016 | 1 | 6.80 | 0.60 | 108 | 15 |
| 1017 | 1 | 12.99 | 1.12 | 109 | 1 |
| 1018 | 1 | 12.10 | 1.04 | 109 | 9 |
| 1019 | 2 | 9.50 | 1.05 | 110 | 14 |
| 1020 | 1 | 10.99 | 0.60 | 110 | 2 |
| 1021 | 1 | 13.99 | 1.33 | 111 | 3 |
| 1022 | 1 | 13.30 | 1.26 | 111 | 11 |
| 1023 | 1 | 12.20 | 0.98 | 112 | 9 |
| 1024 | 1 | 6.40 | 0.51 | 112 | 15 |
| 1025 | 1 | 17.00 | 1.40 | 113 | 12 |
| 1026 | 1 | 11.25 | 0.93 | 113 | 8 |
| 1027 | 1 | 13.69 | 0.96 | 114 | 6 |
| 1028 | 1 | 10.95 | 0.77 | 114 | 8 |
| 1029 | 1 | 11.10 | 0.67 | 115 | 13 |
| 1030 | 2 | 8.95 | 1.07 | 115 | 7 |
| 1031 | 1 | 13.29 | 1.18 | 116 | 1 |
| 1032 | 1 | 10.25 | 0.91 | 116 | 14 |
| 1033 | 1 | 12.99 | 1.12 | 117 | 1 |
| 1034 | 1 | 12.80 | 1.10 | 117 | 11 |
