package org.servalproject.mid;

import android.os.Handler;
import android.util.Log;

import org.servalproject.servaldna.RouteLink;
import org.servalproject.servaldna.ServalDCommand;
import org.servalproject.servaldna.SubscriberId;

/**
 * Created by jeremy on 4/05/16.
 */
public final class Peer implements Comparable<Peer>{
	private static final String TAG ="Peer";

	private static long nextId=0;
	private final long id;

	Peer(Handler handler, SubscriberId sid){
		this.sid = sid;
		observers = new ObserverSet<>(handler, this);
		id = nextId++;
	}

	public final ObserverSet<Peer> observers;
	public final SubscriberId sid;

	ServalDCommand.LookupResult lookup;
	public String getDid(){
		return lookup==null?null:lookup.did;
	}
	public String getName(){
		return lookup==null?null:lookup.name;
	}

	void update(ServalDCommand.LookupResult result){
		lookup = result;
		Log.v(TAG, "Updated details "+result.toString());
		observers.onUpdate();
	}

	RouteLink link;
	public boolean isReachable(){
		return link!=null;
	}

	public boolean isContact(){
		return false;
	}

	public boolean isBlocked(){
		return false;
	}

	void update(RouteLink route){
		link = route.isReachable() ? route : null;
		Log.v(TAG, "Updated route "+route.toString());
		observers.onUpdate();
	}

	public long getId() {
		// return a stable id, for UI list binding.
		return id;
	}

	@Override
	public String toString() {
		return "Peer{" +
				"sid=" + sid +
				", lookup=" + lookup +
				", link=" + link +
				'}';
	}

	public String displayName(){
		String n = getName();
		if (n==null || "".equals(n))
			n = getDid();
		if (n==null || "".equals(n))
			n = sid.abbreviation();
		return n;
	}

	@Override
	public int compareTo(Peer another) {
		return this.displayName().compareTo(another.displayName());
	}
}