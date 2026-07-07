package com.sanikshomemade.phonetomouse.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.sanikshomemade.phonetomouse.MyApp;
import com.sanikshomemade.phonetomouse.PrefUtils;
import com.sanikshomemade.phonetomouse.R;

import java.util.Arrays;
import java.util.Locale;

public class OptionsActivity extends MyApp.MyMultilangCompatActivity {

    @Override
    protected int getResIdForActionBarText() {
        return R.string.entry_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    private void ReplaceFragment(Context confContext) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.settings_fragment_container, new OptionsFragment(confContext), null)
                .commit();
    }

    public static class OptionsFragment extends Fragment {
        private class SettingSwitchCheckListener implements View.OnClickListener {
            private String prefKey;
            public SettingSwitchCheckListener(String prefKey) {
                this.prefKey = prefKey;
            }
            @Override
            public void onClick(View v) {
                putToEditor(((Checkable)v).isChecked(), prefKey);
            }
        }

        private Bitmap cursorBmpBase, cursorBmpSized;
        private boolean cursorScaleChanged = false;
        private SharedPreferences.Editor _editor;
        private Context confContext = null;

        private  <T> void putToEditor(T value, String key) {
            if(_editor == null) {
                _editor = PrefUtils.getEditor(this.getContext());
            }

            if(Boolean.class.isInstance(value)) {
                _editor.putBoolean(key, (boolean)value);
            }
            else if(Integer.class.isInstance(value)) {
                _editor.putInt(key, (int)value);
            }
        }

        public OptionsFragment() {
            super(R.layout.fragment_settings);
        }
        public OptionsFragment(Context confContext) {
            this.confContext = confContext;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if(confContext != null) {
                return LayoutInflater.from(confContext).inflate(R.layout.fragment_settings, container, false);
            }
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ViewGroup themeUiContainer = view.findViewById(R.id.theme_ui);
            Spinner theme_spinner = view.findViewById(R.id.theme_option_picker);
            Switch themeSwitch = view.findViewById(R.id.theme_switch);
            if(Build.VERSION.SDK_INT >= 29) {
                themeUiContainer.removeView(themeSwitch);
                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getContext(), R.array.theme_option_names, android.R.layout.simple_spinner_dropdown_item);
                theme_spinner.setAdapter(adapter1);
                theme_spinner.setSelection(PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.THEME_OPTION, 0));
                theme_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (parent == null) {
                            return;
                        }
                        int crtThemeIndex = PrefUtils.getValueFromPrefs(parent.getContext(), PrefUtils.THEME_OPTION, -1);
                        int themeInRange = Math.max(0, crtThemeIndex);

                        if (themeInRange != position) {
                            putToEditor(position, PrefUtils.THEME_OPTION);
                            AppCompatDelegate.setDefaultNightMode(position == 0 ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM : position);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }
            else {
                themeUiContainer.removeView(theme_spinner);
                themeSwitch.setChecked( PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.THEME_OPTION, false));
                themeSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((Checkable)v).isChecked();
                        putToEditor(checked, PrefUtils.THEME_OPTION);
                        AppCompatDelegate.setDefaultNightMode(checked? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                    }
                });
            }

            ImageView imgv = view.findViewById(R.id.cursor_demo_view);
            cursorBmpBase = BitmapFactory.decodeResource(getResources(), R.drawable.cursor);
            int scaleFactorNow = PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.PREFERRED_CURSOR_SCALE, 20);
            cursorBmpSized = Bitmap.createScaledBitmap(cursorBmpBase,
                    cursorBmpBase.getWidth()/scaleFactorNow,
                    cursorBmpBase.getHeight()/scaleFactorNow, false);
            imgv.setImageBitmap(cursorBmpSized);

            int progressMin = getResources().getInteger(R.integer.cursor_scale_min),
                    progressMax = getResources().getInteger(R.integer.cursor_scale_max);
            int displayProgress = progressMax - scaleFactorNow + progressMin;

            TextView progressText = view.findViewById(R.id.scale_value_text);
            progressText.setText(String.format(Locale.getDefault(),"%d", displayProgress));

            SeekBar _seekbar = view.findViewById(R.id.cursor_scale_seekbar);

            _seekbar.setProgress(Build.VERSION.SDK_INT >= 26? displayProgress : displayProgress-progressMin);
            if(Build.VERSION.SDK_INT >= 26) {
                _seekbar.setMin(progressMin);
                _seekbar.setMax(progressMax);
            }
            else {
                _seekbar.setMax(progressMax-progressMin);
            }
            _seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    cursorScaleChanged = true;
                    int scaleProgress = progressMax - progress;
                    if (Build.VERSION.SDK_INT >= 26) {
                        scaleProgress += progressMin;
                    }
                    int displayProgress = Build.VERSION.SDK_INT >= 26? progress : progress+progressMin;

                    if(cursorBmpSized != null && !cursorBmpSized.isRecycled()) {
                        cursorBmpSized.recycle();
                    }
                    cursorBmpSized = Bitmap.createScaledBitmap(cursorBmpBase,
                            cursorBmpBase.getWidth()/scaleProgress,
                            cursorBmpBase.getHeight()/scaleProgress, false);
                    imgv.setImageBitmap(cursorBmpSized);
                    progressText.setText(String.format(Locale.getDefault(),"%d", displayProgress));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            Spinner languageSpinner = view.findViewById(R.id.language_picker);
            int langIndex = PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.PREFERRED_LANGUAGE, -1);
            if(langIndex>=0) {
                languageSpinner.setSelection(langIndex);
            }
            else {
                String systemLangCode = MyApp.MyMultilangCompatActivity.getLocaleFromRes(Resources.getSystem()).getLanguage();

                String[] translations = this.getContext().getResources().getStringArray(R.array.languages);
                int foundIndex = Arrays.asList(translations).indexOf(systemLangCode.toUpperCase());
                languageSpinner.setSelection(Math.max(0, foundIndex));
            }
            languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (parent == null) {
                        return;
                    }
                    int crtlangIndex = PrefUtils.getValueFromPrefs(parent.getContext(), PrefUtils.PREFERRED_LANGUAGE, -1);

                    if(crtlangIndex != position) {
                        putToEditor(position, PrefUtils.PREFERRED_LANGUAGE);
                        String langCode = (String)parent.getAdapter().getItem(position);
                        Context contextOverridden = PrefUtils.GetOverriddenLanguageContext(langCode, OptionsFragment.this.requireActivity());

                        OptionsActivity parentAct = (OptionsActivity)requireActivity();
                        ActionBar ab = parentAct.getSupportActionBar();
                        if (ab != null) {
                            ab.setTitle(contextOverridden.getResources().getString(R.string.entry_settings));
                        }
                        parentAct.ReplaceFragment(contextOverridden);

                        EntryActivity.setLangCodeForEntry(langCode.toLowerCase());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            Switch reconnect_switch = view.findViewById(R.id.setting_reconnect_switch);
            reconnect_switch.setChecked(PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.PREFERRED_BT_RECONNECT, false));
            reconnect_switch.setOnClickListener(new SettingSwitchCheckListener(PrefUtils.PREFERRED_BT_RECONNECT));

            Switch sound1_switch = view.findViewById(R.id.setting_sound1_switch);
            sound1_switch.setChecked(PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.CONNECT_SOUND_KEY, false));
            sound1_switch.setOnClickListener(new SettingSwitchCheckListener(PrefUtils.CONNECT_SOUND_KEY));

            Switch sound2_switch = view.findViewById(R.id.setting_sound2_switch);
            sound2_switch.setChecked(PrefUtils.getValueFromPrefs(this.getContext(), PrefUtils.LONG_PRESS_SOUND_KEY, true));
            sound2_switch.setOnClickListener(new SettingSwitchCheckListener(PrefUtils.LONG_PRESS_SOUND_KEY));
        }

        @Override
        public void onPause() {
            super.onPause();
            if(cursorScaleChanged) {
                SeekBar _seekbar = this.getView().findViewById(R.id.cursor_scale_seekbar);
                int progressMin = getResources().getInteger(R.integer.cursor_scale_min),
                        progressMax = getResources().getInteger(R.integer.cursor_scale_max);

                int progressToSave = progressMax - _seekbar.getProgress() + progressMin;
                if(Build.VERSION.SDK_INT < 26) { progressToSave += getResources().getInteger(R.integer.cursor_scale_min); }
                putToEditor(progressToSave, PrefUtils.PREFERRED_CURSOR_SCALE);
            }

            if(_editor!=null) {
                _editor.apply();
            }
        }
    }
}