package com.nbehary.retribution;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;


/**
 * Created by nate on 12/10/13.
 */
public class FolderCustomize extends ImageButton {

    private Folder mFolder;

    public FolderCustomize(Context context) {
        super(context);
    }
    public FolderCustomize(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FolderCustomize(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFolder(Folder folder) {
        mFolder = folder;
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.customize_folder, popup.getMenu());
        popup.show();
    }

}
