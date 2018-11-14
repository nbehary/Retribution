/*
 * Copyright (c) 2015. Nathan A. Behary
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.nbehary.retribution;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class IconPickerActivity extends Activity {

    public static final String SELECTED_RESOURCE_EXTRA = "selected_resource";
    public static final String SELECTED_BITMAP_EXTRA = "bitmap";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int iconSize = activityManager.getLauncherLargeIconSize();
        final String pkgName = getIntent().getStringExtra("package");

        GridView gridview = new GridView(this);
        gridview.setNumColumns(GridView.AUTO_FIT);
        gridview.setHorizontalSpacing(40);
        gridview.setVerticalSpacing(40);
        gridview.setPadding(20, 20, 20, 0);
        gridview.setFastScrollEnabled(true);
        gridview.setColumnWidth(iconSize);
        gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        gridview.setAdapter(new ImageAdapter(this, pkgName));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                Intent in = new Intent();
                DrawableInfo d = (DrawableInfo) adapterView.getAdapter().getItem(position);
                in.putExtra(SELECTED_RESOURCE_EXTRA, pkgName + "|" + d.resource_name);
                in.putExtra(SELECTED_BITMAP_EXTRA, ((BitmapDrawable)d.drawable.get()).getBitmap());
                setResult(Activity.RESULT_OK, in);
                finish();
            }
        });
        setContentView(gridview);
    }

    public class ImageAdapter extends BaseAdapter {
        private final Context mContext;
        private Resources mResources;
        private final ArrayList<DrawableInfo> mDrawables = new ArrayList<DrawableInfo>();

        public class FetchDrawable extends AsyncTask<Integer, Void, Drawable> {
            final WeakReference<ImageView> mImageView;

            FetchDrawable(ImageView imgView) {
                mImageView = new WeakReference<ImageView>(imgView);
            }

            @Override
            protected Drawable doInBackground(Integer... position) {
                DrawableInfo info = getItem(position[0]);
                int itemId = info.resource_id;
                Drawable d = ResourcesCompat.getDrawable(mResources,itemId,null);
                info.drawable = new WeakReference<Drawable>(d);
                return d;
            }

            @Override
            public void onPostExecute(Drawable result) {
                if (mImageView.get() != null) {
                    mImageView.get().setImageDrawable(result);
                }
            }
        }

        public ImageAdapter(Context c, String pkgName) {
            mContext = c;
            Map<ComponentName, String> resources = IconPackHelper.getIconPackResources(c, pkgName);
            try {
                mResources = c.getPackageManager().getResourcesForApplication(pkgName);
                LinkedHashSet<String> drawables = new LinkedHashSet<String>(resources.values());
                for (String s : drawables) {
                    int id = mResources.getIdentifier(s, "drawable", pkgName);
                    if (id != 0) {
                        mDrawables.add(new DrawableInfo(s, id));
                    }
                }
            } catch (NameNotFoundException e) {
            }
        }

        public int getCount() {
            return mDrawables.size();
        }

        public DrawableInfo getItem(int position) {
            return mDrawables.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(
                        GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView = (ImageView) convertView;
                Object tag = imageView.getTag();
                if (tag != null && tag instanceof FetchDrawable) {
                    ((FetchDrawable) tag).cancel(true);
                }
            }
            FetchDrawable req = new FetchDrawable(imageView);
            imageView.setTag(req);
            req.execute(position);
            return imageView;
        }
    }

    private class DrawableInfo {
        WeakReference<Drawable> drawable;
        final String resource_name;
        final int resource_id;
        DrawableInfo(String n, int i) {
            resource_name = n;
            resource_id = i;
        }
    }
}
