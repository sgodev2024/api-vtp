"use client";

import { useLayoutEffect, useRef, type InputHTMLAttributes } from "react";

type Props = Omit<InputHTMLAttributes<HTMLInputElement>, "type" | "value" | "onChange"> & {
  value: string | number;
  onChange: (value: string) => void;
};

const normalize = (value: string) => {
  const input = value.replace(/[^\d,.-]/g, "");
  const negative = input.trimStart().startsWith("-");
  const separator = input.indexOf(",");
  const integer = (separator < 0 ? input : input.slice(0, separator)).replace(/[^\d]/g, "");
  const fraction = separator < 0 ? "" : input.slice(separator + 1).replace(/[^\d]/g, "");
  if (!integer && separator < 0) return negative ? "-" : "";
  return `${negative ? "-" : ""}${integer || (separator >= 0 ? "0" : "")}${separator >= 0 ? `.${fraction}` : ""}`;
};

const format = (value: string | number) => {
  const normalized = String(value ?? "");
  const negative = normalized.startsWith("-");
  const [integer = "", fraction] = normalized.replace(/[^\d.]/g, "").split(".", 2);
  const grouped = integer.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
  return `${negative ? "-" : ""}${grouped}${fraction === undefined ? "" : `,${fraction}`}`;
};

export default function MoneyInput({ value, onChange, ...props }: Props) {
  const inputRef = useRef<HTMLInputElement>(null);
  const pendingCaret = useRef<{ digits: number; afterDecimal: boolean } | null>(null);
  const displayValue = format(value);

  useLayoutEffect(() => {
    const caret = pendingCaret.current;
    const input = inputRef.current;
    if (!caret || !input) return;

    let digits = 0;
    for (let index = 0; index < displayValue.length; index++) {
      const character = displayValue[index];
      if (character === "," && caret.afterDecimal && digits >= caret.digits) {
        input.setSelectionRange(index + 1, index + 1);
        pendingCaret.current = null;
        return;
      }
      if (character === "," && !caret.afterDecimal && digits >= caret.digits) {
        input.setSelectionRange(index, index);
        pendingCaret.current = null;
        return;
      }
      if (/\d/.test(character)) {
        if (digits === caret.digits && !caret.afterDecimal) {
          input.setSelectionRange(index, index);
          pendingCaret.current = null;
          return;
        }
        digits++;
      }
    }
    input.setSelectionRange(displayValue.length, displayValue.length);
    pendingCaret.current = null;
  }, [displayValue]);

  return (
    <input
      {...props}
      ref={inputRef}
      type="text"
      inputMode="decimal"
      value={displayValue}
      onChange={event => {
        const input = event.currentTarget;
        const cursor = input.selectionStart ?? input.value.length;
        const preceding = input.value.slice(0, cursor);
        pendingCaret.current = {
          digits: (preceding.match(/\d/g) ?? []).length,
          afterDecimal: preceding.includes(","),
        };
        onChange(normalize(input.value));
      }}
    />
  );
}
