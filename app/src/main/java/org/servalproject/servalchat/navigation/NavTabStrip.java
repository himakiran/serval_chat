package org.servalproject.servalchat.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.servalproject.mid.Identity;
import org.servalproject.mid.Peer;
import org.servalproject.servalchat.R;
import org.servalproject.servaldna.SigningKey;
import org.servalproject.servaldna.Subscriber;

/**
 * Created by jeremy on 7/06/16.
 */
public class NavTabStrip extends LinearLayout implements IContainerView, INavigate {
	NavPageAdapter adapter;
	private ViewPager pager;

	public NavTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void deactivate(ViewState viewState, boolean configChange) {
		// Noop, handled by viewpager
	}

	@Override
	public ViewState activate(Navigation n, Identity identity, Peer peer, Bundle args) {
		for (int i = 0; i < adapter.screens.length; i++) {
			HistoryItem screen = adapter.screens[i];
			if (screen.key.equals(n)) {
				if (pager.getCurrentItem() != i)
					pager.setCurrentItem(i);
				return adapter.getViewState(i);
			}
		}
		throw new IllegalStateException("Cannot locate " + n.name);
	}

	@Override
	public ILifecycle onAttach(MainActivity activity, Navigation n, Identity id, Peer peer, Bundle args) {
		HistoryItem items[] = new HistoryItem[n.children.size()];
		// use the same arguments for all tabs
		SigningKey key = id == null ? null : id.subscriber.signingKey;
		Subscriber subscriber = peer == null ? null : peer.getSubscriber();
		for (int i = 0; i < n.children.size(); i++)
			items[i] = new HistoryItem(n.children.get(i), key, subscriber, args);
		adapter = new NavPageAdapter(activity, id, peer, items);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter.setViewPager(pager);
		TabLayout tabs = (TabLayout) findViewById(R.id.sliding_tabs);
		tabs.setupWithViewPager(pager);
		return adapter;
	}
}
