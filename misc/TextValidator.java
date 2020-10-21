package icn.premierandroid.misc;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Validates an Edit Texts Input
 */

public abstract class TextValidator implements TextWatcher {
    private final TextView textView;

    protected TextValidator(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}