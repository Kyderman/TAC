/**
 * TAC AgentWare
 * http://www.sics.se/tac        tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : 23 April, 2002
 * Updated : $Date: 2005/06/07 19:06:16 $
 *	     $Revision: 1.1 $
 * ---------------------------------------------------------
 * DummyAgent is a simplest possible agent for TAC. It uses
 * the TACAgent agent ware to interact with the TAC server.
 *
 * Important methods in TACAgent:
 *
 * Retrieving information about the current Game
 * ---------------------------------------------
 * int getGameID()
 *  - returns the id of current game or -1 if no game is currently plaing
 *
 * getServerTime()
 *  - returns the current server time in milliseconds
 *
 * getGameTime()
 *  - returns the time from start of game in milliseconds
 *
 * getGameTimeLeft()
 *  - returns the time left in the game in milliseconds
 *
 * getGameLength()
 *  - returns the game length in milliseconds
 *
 * int getAuctionNo()
 *  - returns the number of auctions in TAC
 *
 * int getClientPreference(int client, int type)
 *  - returns the clients preference for the specified type
 *   (types are TACAgent.{ARRIVAL, DEPARTURE, HOTEL_VALUE, E1, E2, E3}
 *
 * int getAuctionFor(int category, int type, int day)
 *  - returns the auction-id for the requested resource
 *   (categories are TACAgent.{CAT_FLIGHT, CAT_HOTEL, CAT_ENTERTAINMENT
 *    and types are TACAgent.TYPE_INFLIGHT, TACAgent.TYPE_OUTFLIGHT, etc)
 *
 * int getAuctionCategory(int auction)
 *  - returns the category for this auction (CAT_FLIGHT, CAT_HOTEL,
 *    CAT_ENTERTAINMENT)
 *
 * int getAuctionDay(int auction)
 *  - returns the day for this auction.
 *
 * int getAuctionType(int auction)
 *  - returns the type for this auction (TYPE_INFLIGHT, TYPE_OUTFLIGHT, etc).
 *
 * int getOwn(int auction)
 *  - returns the number of items that the agent own for this
 *    auction
 *
 * Submitting Bids
 * ---------------------------------------------
 * void submitBid(Bid)
 *  - submits a bid to the tac server
 *
 * void replaceBid(OldBid, Bid)
 *  - replaces the old bid (the current active bid) in the tac server
 *
 *   Bids have the following important methods:
 *    - create a bid with new Bid(AuctionID)
 *
 *   void addBidPoint(int quantity, float price)
 *    - adds a bid point in the bid
 *
 * Help methods for remembering what to buy for each auction:
 * ----------------------------------------------------------
 * int getAllocation(int auctionID)
 *   - returns the allocation set for this auction
 * void setAllocation(int auctionID, int quantity)
 *   - set the allocation for this auction
 *
 *
 * Callbacks from the TACAgent (caused via interaction with server)
 *
 * bidUpdated(Bid bid)
 *  - there are TACAgent have received an answer on a bid query/submission
 *   (new information about the bid is available)
 * bidRejected(Bid bid)
 *  - the bid has been rejected (reason is bid.getRejectReason())
 * bidError(Bid bid, int error)
 *  - the bid contained errors (error represent error status - commandStatus)
 *
 * quoteUpdated(Quote quote)
 *  - new information about the quotes on the auction (quote.getAuction())
 *    has arrived
 * quoteUpdated(int category)
 *  - new information about the quotes on all auctions for the auction
 *    category has arrived (quotes for a specific type of auctions are
 *    often requested at once).

 * auctionClosed(int auction)
 *  - the auction with id "auction" has closed
 *
 * transaction(Transaction transaction)
 *  - there has been a transaction
 *
 * gameStarted()
 *  - a TAC game has started, and all information about the
 *    game is available (preferences etc).
 *
 * gameStopped()
 *  - the current game has ended
 *
 */

package se.sics.tac.aw;
import se.sics.tac.util.ArgEnumerator;
import java.util.logging.*;
import java.util.TreeMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;


public class DummyAgent extends AgentImpl {

  private static final Logger log =
    Logger.getLogger(DummyAgent.class.getName());

  private static final boolean DEBUG = false;

  private float[] prices;
  private List<Intention> intentions = new ArrayList<Intention>();
  private boolean hotelsAllocated = false;

  protected void init(ArgEnumerator args) {
    prices = new float[agent.getAuctionNo()];
  }

  public void quoteUpdated(Quote quote) {
    int auction = quote.getAuction();
    int auctionCategory = agent.getAuctionCategory(auction);
    List<Intention> auctionIntentions = getIntentionsForAuction(auction);
    
    if(auctionIntentions.size() > 0) {
      if (auctionCategory == TACAgent.CAT_FLIGHT) {
        for (Intention intention : auctionIntentions) {
          intention.isAcquired = true;
          intention.currentBid = quote.getAskPrice();
        }
      } else if (auctionCategory == TACAgent.CAT_HOTEL) {
       

       
      } else if (auctionCategory == TACAgent.CAT_ENTERTAINMENT) {
        /*int alloc = auctionIntentions.length - agent.getOwn(auction);
        if (alloc != 0) {
          Bid bid = new Bid(auction);
          if (alloc < 0) {
            prices[auction] = 200f - (agent.getGameTime() * 120f) / 720000;
          }
          else {
            prices[auction] = 50f + (agent.getGameTime() * 100f) / 720000;
          }

          bid.addBidPoint(alloc, prices[auction]);
          agent.submitBid(bid);
        }*/
      }
    }
  }

  public void quoteUpdated(int auctionCategory) {
    log.fine("All quotes for "
	     + agent.auctionCategoryToString(auctionCategory)
	     + " has been updated");

    // if flights allocated
    if (auctionCategory == TACAgent.CAT_FLIGHT) {
      if(isFlightsAcquired() && !hotelsAllocated) {
        // Allocate hotels based on prices paid for flights
        sendHotelBids();
        hotelsAllocated = true;
      }
    }
  }

  public void bidUpdated(Bid bid) {
    log.fine("Bid Updated: id=" + bid.getID() + " auction="
	     + bid.getAuction() + " state="
	     + bid.getProcessingStateAsString());
    log.fine("       Hash: " + bid.getBidHash());
  }

  public void bidRejected(Bid bid) {
    log.warning("Bid Rejected: " + bid.getID());
    log.warning("      Reason: " + bid.getRejectReason()
		+ " (" + bid.getRejectReasonAsString() + ')');
  }

  public void bidError(Bid bid, int status) {
    log.warning("Bid Error in auction " + bid.getAuction() + ": " + status
		+ " (" + agent.commandStatusToString(status) + ')');
  }

  public void gameStarted() {
    log.fine("Game " + agent.getGameID() + " started!");
    hotelsAllocated = false;
    calculateIntentions();
    sendBids();
  }

  public void gameStopped() {
    log.fine("Game Stopped!");
  }

  public void auctionClosed(int auction) {
    log.fine("*** Auction " + auction + " closed!");

    int auctionCategory = agent.getAuctionCategory(auction);
    List<Intention> auctionIntentions = getIntentionsForAuction(auction);
    
    if(auctionIntentions.size() > 0) {
     
      if (auctionCategory == TACAgent.CAT_HOTEL) {
        int alloc = auctionIntentions.size();
        int have = agent.getOwn(auction);
        log.finer("alloc: " + alloc + " have: " + have);
        
        // using have work out the "have" highest bids, mark them as acquired
        if(have == alloc) {
          log.finer("have all allocations");
          for (Intention intention : auctionIntentions) {
            if(!intention.isAcquired){
              log.finer("setting as acquired");
              intention.isAcquired = true;
            } else {
              log.finer("already set as acquired");
            }
          }
        } else if (have > 0 ) {
          log.finer("have some of our allocations: " + have);
          Map<Intention, Float> valueMap = new LinkedHashMap<Intention, Float>();
          for (Intention intention : auctionIntentions) {
            if(!intention.isAcquired) {
              valueMap.put(intention, intention.currentBid);
              log.finer("IN: " + intention.currentBid);
            } else {
              log.finer("OUT already acquired: " + intention.currentBid);
            }
          }

          Map<Intention, Float> sortedValueMap = sortByValue(valueMap);

          int cur = 0;
          for(Intention intention : sortedValueMap.keySet()) {
            intention.isAcquired = true;
            log.finer("ACQUIRED");
            cur++;
            if(cur == have) {
              break;
            }
          }
        }
      }
    }
  }

  private void sendBids() {

    for (int i = 0, n = agent.getAuctionNo(); i < n; i++) {
      List<Intention> auctionIntentions = getIntentionsForAuction(i);
      if(auctionIntentions.size() > 0) {
        Bid bid = new Bid(i);

        switch (agent.getAuctionCategory(i)) {
          case TACAgent.CAT_FLIGHT:
            for (Intention intention : auctionIntentions) {
              bid.addBidPoint(1, 500);
              intention.currentBid = 500;
              intention.maximumBid = 500;
            }
          break;
          case TACAgent.CAT_HOTEL:
            /*for (Intention intention : auctionIntentions) {
              bid.addBidPoint(1, 200);
              intention.currentBid = 200;
              intention.maximumBid = 400;
            }*/
          break;
          case TACAgent.CAT_ENTERTAINMENT:
            for (Intention intention : auctionIntentions) {
              bid.addBidPoint(1, 70);
              intention.currentBid = 70;
              intention.maximumBid = 200;
            }
          break;
        }
        agent.submitBid(bid);
      }
    }
  }

  public void transaction(Transaction transaction) {
    int auction = transaction.getAuction();
    int auctionCategory = agent.getAuctionCategory(auction);
    List<Intention> auctionIntentions = getIntentionsForAuction(auction);

    if(auctionIntentions.size() > 0) {
      if (auctionCategory == TACAgent.CAT_HOTEL) {
        for (Intention intention : auctionIntentions) {
           intention.finalPrice = transaction.getPrice();
        }
      }
    }
  }

  private void sendHotelBids() {
    for (int i = 0, n = agent.getAuctionNo(); i < n; i++) {
      if (agent.getAuctionCategory(i) == TACAgent.CAT_HOTEL) {
        Bid bid = new Bid(i);
        for (Intention intention : getIntentionsForAuction(i)) {
          bid.addBidPoint(1, getMaxHotelSpend(intention.customerId));
        }
        agent.submitBid(bid);
      }
    }
  }

  private boolean isGoodHotel(int client) {
  	int inFlight = agent.getClientPreference(client, TACAgent.ARRIVAL);
    int outFlight = agent.getClientPreference(client, TACAgent.DEPARTURE);
    int stayLength = outFlight - inFlight;
    int hotel = agent.getClientPreference(client, TACAgent.HOTEL_VALUE);
    boolean goodHotel = false;

    if (stayLength == 1) {
      	goodHotel = true;
    } else if (stayLength == 2) {
      if(hotel >= 90) {
      	goodHotel = true;
      }
    } else if (stayLength == 3) {
      if(hotel >= 130) {
      	goodHotel = true;
      }
    }

    return goodHotel;
  }

  private void calculateIntentions() {
    intentions.clear();
    for (int i = 0; i < 8; i++) {
      int inFlight = agent.getClientPreference(i, TACAgent.ARRIVAL);
      int outFlight = agent.getClientPreference(i, TACAgent.DEPARTURE);
      int bonus = agent.getClientPreference(i, TACAgent.HOTEL_VALUE);
      int type;

      // Get the flight preferences auction and remember that we are
      // going to buy tickets for these days. (inflight=1, outflight=0)
      int auction = agent.getAuctionFor(TACAgent.CAT_FLIGHT,
          TACAgent.TYPE_INFLIGHT, inFlight);

      intentions.add(new Intention(i, 0, auction));

      auction = agent.getAuctionFor(TACAgent.CAT_FLIGHT,
            TACAgent.TYPE_OUTFLIGHT, outFlight);

      intentions.add(new Intention(i, 0, auction));


      if (isGoodHotel(i)) {
      type = TACAgent.TYPE_GOOD_HOTEL;
      } else {
      type = TACAgent.TYPE_CHEAP_HOTEL;
      }
      // allocate a hotel night for each day that the agent stays
      for (int d = inFlight; d < outFlight; d++) {
        auction = agent.getAuctionFor(TACAgent.CAT_HOTEL, type, d);
        log.finer("Adding hotel for day: " + d + " on " + auction);
        intentions.add(new Intention(i, bonus, auction));
      }

      entertainmentSolver(inFlight, outFlight, i);
    }
  }

  private void entertainmentSolver(int in, int out, int client) {
    int stay = out - in;

    Map<Integer,Integer> map = new TreeMap<Integer,Integer>();
    int e1 = agent.getClientPreference(client, TACAgent.E1);
    int e2 = agent.getClientPreference(client, TACAgent.E2);
    int e3 = agent.getClientPreference(client, TACAgent.E3);

    map.put(TACAgent.TYPE_ALLIGATOR_WRESTLING, e1);
    map.put(TACAgent.TYPE_AMUSEMENT, e2);
    map.put(TACAgent.TYPE_MUSEUM, e3);

    Map<Integer, Integer> entMap = sortByValue(map);

    int cur = 0;
    for(Integer i : entMap.keySet()) {
      log.finer("client: " + client + " - entertainment " + i + " value " + entMap.get(i));
      int auction = agent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, i, in + cur);
      intentions.add(new Intention(client, entMap.get(i), auction));
      cur++;
      if(cur == stay) {
        break;
      }
    }
      
  }
  


  public static Map sortByValue(Map unsortMap) {   
  List list = new LinkedList(unsortMap.entrySet());
 
  Collections.sort(list, new Comparator() {
    public int compare(Object o1, Object o2) {
      return ((Comparable) ((Map.Entry) (o2)).getValue())
            .compareTo(((Map.Entry) (o1)).getValue());
    }
  });
 
  Map sortedMap = new LinkedHashMap();
  for (Iterator it = list.iterator(); it.hasNext();) {
    Map.Entry entry = (Map.Entry) it.next();
    sortedMap.put(entry.getKey(), entry.getValue());
  }
  return sortedMap;
}

  // -------------------------------------------------------------------
  // Only for backward compability
  // -------------------------------------------------------------------

  public static void main (String[] args) {
    TACAgent.main(args);
  }

  public float getMaxHotelSpend(int customerId) {
    float spent = getSpentOnFlights(customerId);
    float toSpend = 1000 - spent;
    int inFlight = agent.getClientPreference(customerId, TACAgent.ARRIVAL);
    int outFlight = agent.getClientPreference(customerId, TACAgent.DEPARTURE);
    int hotelCount = outFlight - inFlight;
    return (toSpend / hotelCount);
  }

  public List<Intention> getIntentionsForAuction(int auctionId) {
    List<Intention> inten = new ArrayList<Intention>();
    for (Intention intention : intentions) {
      if(intention.auctionId == auctionId) {
        inten.add(intention);
      }
    }
    return inten;
  }

  public int getOwned(int auctionId) {
    int ownedCount = 0;
    for (Intention intention : getIntentionsForAuction(auctionId)) {
      if(intention.isAcquired) {
        ownedCount++;
      }

    }
    return ownedCount;
  }

  public List<Intention> getIntentionsForCustomer(int customerId) {
    List<Intention> inten = new ArrayList<Intention>();
    for (Intention intention : intentions) {
      if(intention.customerId == customerId) {
        inten.add(intention);
      }
    }
    return inten;
  }

  public float getSpentOnFlights(int customerId) {
    float spent = 0;
    for (Intention intention : getFlightIntentions(customerId)) {
      spent += intention.currentBid;
    }
    return spent;
  }

  public List<Intention> getFlightIntentions(int customerId) {
    List<Intention> inten = new ArrayList<Intention>();
    for (Intention intention : intentions) {
      if((intention.customerId == customerId) && (agent.getAuctionCategory(intention.auctionId) == TACAgent.CAT_FLIGHT)) {
        inten.add(intention);
      }
    }
    return inten;
  }

  public boolean isFlightsAcquired() {
    for (Intention intention : intentions) {
      if((agent.getAuctionCategory(intention.auctionId) == TACAgent.CAT_FLIGHT)) {
        if(!intention.isAcquired) {
          return false;
        }
      }
    }
    return true;
  }

  class Intention {
	  public int auctionId;
    public float finalPrice;
    public int customerId;
    public float currentBid;
    public float maximumBid;
    public int bonusValue;
    public boolean isSell;
    public boolean isAcquired;

    public Intention(int cId, int bV, int aId) {
      auctionId = aId;
      customerId = cId;
      currentBid = -1;
      maximumBid = -1;
      bonusValue = bV;
      isSell = false;
      isAcquired = false;
      finalPrice = -1;
    }
  }

  

} // DummyAgent
