package com.nbehary.retribution;

import android.app.FragmentTransaction;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;



/**
 * Created by nate on 12/10/13.
 */
public class FolderCustomize extends ImageButton implements PopupMenu.OnMenuItemClickListener {

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
        popup.setOnMenuItemClickListener(this);
        popup.show();
//        chooseColor();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.folder_indv_color_chooser:
                chooseColor();
                return true;
            case R.id.folder_add_shortcuts:
                addShortcuts();
                return true;
        }

        return false;
    }

    private void chooseColor() {
        FragmentTransaction ft = mFolder.mLauncher.getFragmentManager().beginTransaction();

        // Create and show the dialog.
        FolderColorsDialogFragment folderDialog =  new FolderColorsDialogFragment();
        folderDialog.setmFolder(mFolder);
        folderDialog.show(ft, "dialog");


        //LauncherModel.updateItemInDatabase(mFolder.mLauncher, mFolder.mInfo);

    }

    private void addShortcuts() {
        //Toast toast = Toast.makeText(this.getContext(), "Not implemented", Toast.LENGTH_SHORT);
        //toast.show();
        FragmentTransaction ft = mFolder.mLauncher.getFragmentManager().beginTransaction();
        FolderAddDialogFragment addDialogFragment = new FolderAddDialogFragment();
        addDialogFragment.setFolder(mFolder);
        addDialogFragment.show(ft,"addDialog");
    }

}
