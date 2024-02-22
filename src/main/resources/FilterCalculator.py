import tkinter as tk
from tkinter import filedialog, messagebox, simpledialog
import pandas as pd
from openpyxl.styles import Font
import numpy as np


class FilterCalculator:
    def __init__(self, df, filter_value):
        self.df = df
        self.filter_value = filter_value
        self.step_percent = 70

    def filter_and_calculate(self, range_percent, step_percent):
        filtered_df = self.df[self.df['Вид работ по справочнику'] == self.filter_value].copy()
        if filtered_df.empty:
            return None, None, None, None, None, None, None

        Q1 = filtered_df['Трудоемкость'].quantile(0.25)
        Q3 = filtered_df['Трудоемкость'].quantile(0.75)
        IQR = Q3 - Q1

        lower_bound = Q1 - 1.5 * IQR
        upper_bound = Q3 + 1.5 * IQR

        values_within_iqr = filtered_df[
            (filtered_df['Трудоемкость'] >= lower_bound) & (filtered_df['Трудоемкость'] <= upper_bound)][
            'Трудоемкость']
        step = np.median(values_within_iqr) * (step_percent / 100)

        max_value = filtered_df['Трудоемкость'].max()
        max_value_within_iqr = np.median(values_within_iqr) + step * np.floor(
            (max_value - np.median(values_within_iqr)) / step)

        ranges = np.arange(0, max_value_within_iqr + step, step)

        filtered_df['Range'] = pd.cut(filtered_df['Трудоемкость'], bins=ranges, right=True)

        count_in_ranges = filtered_df[
            (filtered_df['Трудоемкость'] >= lower_bound) & (filtered_df['Трудоемкость'] <= upper_bound)].copy()
        count_in_ranges = count_in_ranges['Range'].value_counts().reset_index()
        count_in_ranges.columns = ['Range', 'Count']

        max_count_range = count_in_ranges.loc[count_in_ranges['Count'].idxmax(), 'Range']
        selected_values = filtered_df[filtered_df['Range'] == max_count_range]['Трудоемкость']

        selected_mean_values = filtered_df[filtered_df['Range'].isin(
            count_in_ranges[
                count_in_ranges['Count'] > (range_percent / 100) * count_in_ranges['Count'].max()]['Range'])][
            'Трудоемкость']

        if not selected_mean_values.empty:
            mean_value = selected_mean_values.mean()
            count_in_ranges.loc[count_in_ranges['Range'] == max_count_range, 'Mean'] = mean_value
        else:
            mean_value = selected_values.mean()
            count_in_ranges['Mean'] = np.where(count_in_ranges['Range'] == max_count_range, mean_value, None)

        filtered_df['IQR'] = IQR
        filtered_df['Within IQR'] = np.where(
            (filtered_df['Трудоемкость'] >= lower_bound) & (filtered_df['Трудоемкость'] <= upper_bound),
            filtered_df['Трудоемкость'],
            ""
        )
        filtered_df['Lower Bound'] = lower_bound
        filtered_df['Upper Bound'] = upper_bound

        return filtered_df, count_in_ranges, mean_value, Q1, Q3, lower_bound, upper_bound


class FilterCalculatorApp:
    def __init__(self, master):
        self.master = master
        self.master.title("Расчет нормативной трудоёмкости")

        self.uvr_file = None
        self.stg_file = None
        self.result_directory = None
        self.range_percent = 75
        self.step_percent = 70
        self.usecols_value = 'С'

        self.uvr_label = tk.Label(self.master, text="Выберите файл 'Справочник':")
        self.uvr_label.pack(pady=10)

        self.uvr_file_button = tk.Button(self.master, text="Обзор", command=self.choose_uvr_file)
        self.uvr_file_button.pack(pady=10)

        self.usecols_label = tk.Label(self.master, text="Введите столбец 'Вид работ':")
        self.usecols_label.pack(pady=10)

        self.usecols_entry = tk.Entry(self.master)
        self.usecols_entry.insert(0, self.usecols_value)
        self.usecols_entry.pack(pady=10)

        self.stg_label = tk.Label(self.master, text="Выберите файл 'Сводки':")
        self.stg_label.pack(pady=10)

        self.stg_file_button = tk.Button(self.master, text="Обзор", command=self.choose_stg_file)
        self.stg_file_button.pack(pady=10)

        self.result_label = tk.Label(self.master, text="Выберите папку для сохранения файла Result:")
        self.result_label.pack(pady=10)

        self.result_directory_button = tk.Button(self.master, text="Обзор", command=self.choose_result_directory)
        self.result_directory_button.pack(pady=10)

        self.range_label = tk.Label(self.master, text="Сравнение диапазонов по количеству значений \n(range_percent, в процентах):")
        self.range_label.pack(pady=10)

        self.range_entry = tk.Entry(self.master)
        self.range_entry.insert(0, self.range_percent)
        self.range_entry.pack(pady=10)

        self.step_label = tk.Label(self.master, text="Шаг деления на диапазоны \n(step_percent, в процентах):")
        self.step_label.pack(pady=10)

        self.step_entry = tk.Entry(self.master)
        self.step_entry.insert(0, self.step_percent)
        self.step_entry.pack(pady=10)

        self.calculate_button = tk.Button(self.master, text="Выполнить расчет", command=self.calculate_and_display_results)
        self.calculate_button.pack(pady=10)

    def choose_uvr_file(self):
        file_path = filedialog.askopenfilename(filetypes=[("Excel files", "*.xlsx;*.xls")])
        if file_path:
            self.uvr_file = file_path
            messagebox.showinfo("Успех", "Файл 'Справочник' выбран!")

    def choose_stg_file(self):
        file_path = filedialog.askopenfilename(filetypes=[("Excel files", "*.xlsx;*.xls")])
        if file_path:
            self.stg_file = file_path
            messagebox.showinfo("Успех", "Файл 'Сводки' выбран!")

    def choose_result_directory(self):
        self.result_directory = filedialog.askdirectory()
        messagebox.showinfo("Успех", "Папка для сохранения файла Result.xlsx выбрана!")

    def calculate_and_display_results(self):
        try:
            self.range_percent = float(self.range_entry.get())
            self.step_percent = float(self.step_entry.get())
            self.usecols_value = self.usecols_entry.get()
        except ValueError:
            messagebox.showerror("Ошибка", "Пожалуйста, введите корректные числовые значения для процентов.")
            return

        if not all([self.uvr_file, self.stg_file, self.result_directory]):
            messagebox.showwarning("Внимание", "Выберите все необходимые файлы и директории!")
            return

        try:
            uvr_df = pd.read_excel(self.uvr_file, usecols = self.usecols_value, skiprows=1, header=None)
            filter_values = uvr_df.iloc[:, 0].tolist()
            stg_df = pd.read_excel(self.stg_file, sheet_name='Сводки (люди чел.час)')
        except Exception as e:
            messagebox.showerror("Ошибка", f"Ошибка при загрузке данных: {e}")
            return

        result_path = f"{self.result_directory}/Result.xlsx"

        with pd.ExcelWriter(result_path, engine='openpyxl') as writer:
            summary_sheet = writer.book.create_sheet('Summary')
            summary_sheet['A1'] = 'Filter'
            summary_sheet['B1'] = 'Mean'
            summary_sheet['C1'] = 'Count values within IQR'

            for idx, filter_value in enumerate(filter_values):
                calculator = FilterCalculator(stg_df, filter_value)
                _, count_in_ranges, mean_value, _, _, _, _ = calculator.filter_and_calculate(
                    self.range_percent, self.step_percent)
                summary_sheet[f'A{idx + 2}'] = filter_value
                summary_sheet[f'B{idx + 2}'] = mean_value

                if count_in_ranges is not None:
                    count_values_sum = count_in_ranges['Count'].sum()
                    summary_sheet[f'C{idx + 2}'] = count_values_sum

            summary_sheet.delete_rows(2)

            for idx, filter_value in enumerate(filter_values):
                calculator = FilterCalculator(stg_df, filter_value)
                filtered_df, count_in_ranges, _, Q1, Q3, lower_bound, upper_bound = calculator.filter_and_calculate(
                    self.range_percent, self.step_percent)

                if filtered_df is None:
                    continue

                sheet_name = f"Result_{idx + 1}"
                sheet = writer.book.create_sheet(title=sheet_name)
                sheet['G1'] = filter_value
                sheet['G1'].font = Font(bold=True)

                sheet['G2'] = 'IQR'
                sheet['H2'] = filtered_df['IQR'].iloc[0]
                sheet['G3'] = 'Q1'
                sheet['H3'] = Q1
                sheet['G4'] = 'Q3'
                sheet['H4'] = Q3
                sheet['G5'] = 'Lower Bound'
                sheet['H5'] = lower_bound
                sheet['G6'] = 'Upper Bound'
                sheet['H6'] = upper_bound

                count_in_ranges.to_excel(writer, index=False, sheet_name=sheet_name, startrow=8)

                selected_mean_values = filtered_df[filtered_df['Range'].isin(
                    count_in_ranges[count_in_ranges['Count'] > (self.range_percent / 100) * count_in_ranges[
                        'Count'].max()]['Range'])]['Трудоемкость']

                values_for_mean_df = pd.DataFrame({
                    'Values for Mean': selected_mean_values,
                    'Calculation Comment': 'Used for mean calculation'
                })

                values_for_mean_df.to_excel(writer, index=False, sheet_name=sheet_name, startcol=3, startrow=8)

                all_values_df = pd.DataFrame({'All Values': filtered_df['Трудоемкость']})
                all_values_df.to_excel(writer, index=False, sheet_name=sheet_name, startcol=4, startrow=8)

                values_within_iqr_df = pd.DataFrame({'Values within IQR': filtered_df['Within IQR']})
                values_within_iqr_df.to_excel(writer, index=False, sheet_name=sheet_name, startcol=6, startrow=8)

        messagebox.showinfo("Успех", "Расчет завершен! Результаты сохранены.")


def main():
    root = tk.Tk()
    app = FilterCalculatorApp(root)
    root.mainloop()


if __name__ == "__main__":
    main()
