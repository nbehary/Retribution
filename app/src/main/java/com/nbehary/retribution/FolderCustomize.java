package com.nbehary.retribution;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
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
//        PopupMenu popup = new PopupMenu(this.getContext(), v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.customize_folder, popup.getMenu());
//        popup.setOnMenuItemClickListener(this);
//        popup.show();
        chooseColor();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.folder_indv_color_chooser:
                chooseColor();
                return true;
        }

        return false;
    }

    private void chooseColor() {
        Log.d("nbehary112", "Clicky!"+mFolder.mInfo.title);
       // mFolder.mInfo.customColors = 1;
        FragmentTransaction ft = mFolder.mLauncher.getFragmentManager().beginTransaction();
        //Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        //if (prev != null) {
       //     ft.remove(prev);
       // }
       // ft.addToBackStack(null);

        // Create and show the dialog.
        FolderColorsDialogFragment folderDialog =  new FolderColorsDialogFragment();
        folderDialog.setmFolder(mFolder);
        folderDialog.show(ft, "dialog");


        //LauncherModel.updateItemInDatabase(mFolder.mLauncher, mFolder.mInfo);

    }

}
