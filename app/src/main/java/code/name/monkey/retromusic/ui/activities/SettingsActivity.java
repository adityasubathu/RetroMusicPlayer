package code.name.monkey.retromusic.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import code.name.monkey.appthemehelper.ThemeStore;
import code.name.monkey.appthemehelper.util.ColorUtil;
import code.name.monkey.retromusic.R;
import code.name.monkey.retromusic.appshortcuts.DynamicShortcutManager;
import code.name.monkey.retromusic.ui.activities.base.AbsBaseActivity;
import code.name.monkey.retromusic.ui.fragments.settings.MainSettingsFragment;
import code.name.monkey.retromusic.util.PreferenceUtil;

public class SettingsActivity extends AbsBaseActivity implements ColorChooserDialog.ColorCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.detail_content_frame)
    @Nullable
    FrameLayout detailsFrame;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        switch (dialog.getTitle()) {
            case R.string.primary_color:
                int theme = ColorUtil.isColorLight(selectedColor) ?
                        PreferenceUtil.getThemeResFromPrefValue("light") :
                        PreferenceUtil.getThemeResFromPrefValue("dark");

                ThemeStore.editTheme(this).activityTheme(theme).primaryColor(selectedColor).commit();
                break;
            case R.string.accent_color:
                ThemeStore.editTheme(this).accentColor(selectedColor).commit();
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).updateDynamicShortcuts();
        }
        recreate();
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();
        setLightNavigationBar(true);

        setupToolbar();

        if (bundle == null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MainSettingsFragment())
                    .commit();
        }
    }

    private void setupToolbar() {
        title.setTextColor(ThemeStore.textColorPrimary(this));
        int primaryColor = ThemeStore.primaryColor(this);
        appBarLayout.setBackgroundColor(primaryColor);
        toolbar.setBackgroundColor(primaryColor);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setTitle(null);
        setSupportActionBar(toolbar);
    }


    public void setupFragment(Fragment fragment, @StringRes int titleName) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_up, 0, 0, R.animator.slide_down);

        title.setText(titleName);

        if (detailsFrame == null) {
            fragmentTransaction.replace(R.id.content_frame, fragment, fragment.getTag());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.detail_content_frame, fragment, fragment.getTag());
            fragmentTransaction.commit();
        }


    }


    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            title.setText(R.string.action_settings);
            fragmentManager.popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addAppbarLayoutElevation(float v) {
        TransitionManager.beginDelayedTransition(appBarLayout);
        appBarLayout.setElevation(v);
    }
}
