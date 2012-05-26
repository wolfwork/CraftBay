/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2012 StarTux
 *
 * This file is part of CraftBay.
 *
 * CraftBay is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CraftBay is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CraftBay.  If not, see <http://www.gnu.org/licenses/>.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.self.startux.craftBay;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ItemDelivery implements ConfigurationSerializable {
        private Merchant recipient;
        private Item item;
        private Auction auction;
        private Date creationDate;

        public ItemDelivery(Merchant recipient, Item item, Auction auction, Date creationDate) {
                this.recipient = recipient;
                this.item = item;
                this.auction = auction;
                this.creationDate = creationDate;
        }

        public boolean deliver() {
                boolean result = item.give(recipient);
                if (result) {
                        System.out.println("Delivery successful: " + item + " to " + recipient.getName());
                } else {
                        System.out.println("Delivery failed: " + item + " to " + recipient.getName());
                }
                return result;
        }

        public Merchant getRecipient() {
                return recipient;
        }

        public Date getCreationDate() {
                return new Date(creationDate.getTime());
        }

        /**
         * Schedule a delivery as soon as possible, even immediately.
         * @param recipient the recipient
         * @param item the item
         * @param auction the associated Auction instance
         * @return The ItemDelivery instance if it had to be
         * scheduled for the future, null otherwise
         */
        public static ItemDelivery schedule(Merchant recipient, Item item, Auction auction) {
                ItemDelivery delivery = new ItemDelivery(recipient, item, auction, new Date());
                if (!delivery.deliver()) {
                        CraftBayPlugin.getInstance().getAuctionScheduler().queueDelivery(delivery);
                        return delivery;
                }
                return null;
        }

        public static void deliverAll() {
                CraftBayPlugin.getInstance().getAuctionScheduler().checkDeliveries();
        }

        public Map<String, Object> serialize() {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("recipient", recipient.clone());
                result.put("item", item.clone());
                if (auction != null) result.put("auction", auction.getUID());
                result.put("created", creationDate.getTime());
                return result;
        }

        public static ItemDelivery deserialize(Map<String, Object> map) {
                Merchant recipient = (Merchant)map.get("recipient");
                Item item = (Item)map.get("item");
                Auction auction = null;
                if (map.get("auction") != null) {
                        auction = CraftBayPlugin.getInstance().getAuctionScheduler().getByUID((Long)map.get("auction"));
                }
                Date creationDate = new Date((Long)map.get("created"));
                return new ItemDelivery(recipient, item, auction, creationDate);
        }
}