package jiro.lib.javafx.scene.control;

import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;

/**
 * 数値のみ入力可能な独自の拡張テキストフィールドクラス。<br>
 * <p>
 * 拡張した機能:<br>
 * <ul>
 * <li>マウスホイールで入力されている数値をインクリメント・デクリメントできる。<br>
 * この時、Ctrlを押しながらで少し、Shiftを押しながらで大きく変動させられる。</li>
 * <li>入力できる数値に下限・上限を設定できる。</li>
 * <li>上限値を元に入力できる桁数を設定する。</li>
 * </ul>
 * @author jiro
 * @version 1.3
 */
public class NumericTextField extends TextField {
  private static final String TEXT = "0";
  private static final int MIN = 0;
  private static final int MAX = 100;
  private static final int DEFAULT_VALUE = 0;

  /**
   * 入力可能な数の下限
   */
  private final int min;

  /**
   * 入力可能な数の上限
   */
  private final int max;

  /**
   * 値が存在しなかった場合の初期値
   */
  private final int defaultValue;

  /**
   * マウススクロールでの数値の変動量
   */
  private int variationValue = 5;

  /**
   * マウススクロールでの数値の大きい変動量
   */
  private int largeVariationValue = 10;

  /**
   * デフォルト設定を利用するコンストラクタ。
   */
  public NumericTextField() {
    this(TEXT, MIN, MAX, DEFAULT_VALUE);
  }

  /**
   * コンストラクタ。
   * @param text 初期テキスト
   */
  public NumericTextField(String text) {
    this(text, MIN, MAX, DEFAULT_VALUE);
  }

  /**
   * コンストラクタ。
   * @param aMin 入力可能数値の下限
   * @param aMax 入力可能数値の上限
   */
  public NumericTextField(int aMin, int aMax) {
    this(TEXT, aMin, aMax, DEFAULT_VALUE);
  }

  /**
   * コンストラクタ。
   * @param text 初期テキスト
   * @param aMin 入力可能数値の下限
   * @param aMax 入力可能数値の上限
   */
  public NumericTextField(String text, int aMin, int aMax) {
    this(text, aMin, aMax, DEFAULT_VALUE);
  }

  /**
   * コンストラクタ。
   * @param text 初期テキスト
   * @param aMin 入力可能数値の下限
   * @param aMax 入力可能数値の上限
   * @param aDefaultValue 値が存在しなかった場合に設定されるデフォルト値
   */
  public NumericTextField(String text, int aMin, int aMax, int aDefaultValue) {
    super(text);
    min = aMin;
    max = aMax;
    defaultValue = aDefaultValue;

    // 数値のみ入力可能に設定する
    textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("[-]?[0-9]*")) {
        setText(oldValue);
      }
    });

    // 入力可能文字数上限を設定する
    lengthProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue.intValue() < newValue.intValue()) {
        int minLength = String.valueOf(min).length();
        int maxLength = String.valueOf(max).length();
        int digit = Math.max(minLength, maxLength);
        if (digit <= getText().length()) {
          setText(getText().substring(0, digit));
        }
      }
    });

    // マウススクロールで数値のインクリメント・デクリメントを可能にする
    setOnScroll(e -> changeValueWithScroll(e));
  }

  /**
   * マウススクロールで保持する数値をインクリメント・デクリメントする。
   * @param e ScrollEvent
   */
  public final void changeValueWithScroll(ScrollEvent e) {
    setDefaultValueIfEmpty();
    int value = Integer.valueOf(getText());
    int number =
        e.isControlDown() ?
            0 < e.getDeltaY() ? value+variationValue : value-variationValue
        : e.isShiftDown() ?
            0 < e.getDeltaX() ? value+largeVariationValue : value-largeVariationValue
        : 0 < e.getDeltaY() ? value+1 : value-1;
    number = Math.min(number, max);
    number = Math.max(min, number);
    setText("" + number);
  }

  /**
   * 保持している数値を返す。
   * @return 数値
   */
  public int getNumber() {
    setDefaultValueIfEmpty();
    return Integer.valueOf(getText());
  }

  /**
   * 最小値を返す。
   * @return 最小値
   */
  public int getMin() {
    return min;
  }

  /**
   * 最大値を返す。
   * @return 最大値
   */
  public int getMax() {
    return max;
  }

  /**
   * マウススクロールでの数値の変動量をセットする。
   * @param variationValue 変動量
   */
  public void setVariationValue(int variationValue) {
    this.variationValue = variationValue;
  }

  /**
   * マウススクロールでの数値の大きい変動量をセットする。
   * @param largeVariationValue 変動量
   */
  public void setLargeVariationValue(int largeVariationValue) {
    this.largeVariationValue = largeVariationValue;
  }

  /**
   * 保持するテキストが""かnullだった場合に、初期値をセットする。
   */
  private void setDefaultValueIfEmpty() {
    if (getText().equals("") || getText() == null) {
      setText("" + defaultValue);
    }
  }
}
