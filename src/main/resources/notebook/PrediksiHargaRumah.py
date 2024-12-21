import tensorflow as tf
import pandas as pd
import numpy as np
import os
from sklearn.preprocessing import StandardScaler
import sys

# Menonaktifkan log non-error dari TensorFlow
tf.get_logger().setLevel('ERROR')

# Path untuk menyimpan bobot model
model_weights_path = 'model_rumah.weights.h5'

# Membaca data
data = pd.read_csv('C:/tugasakhir/prediksihargarumah/src/main/resources/notebook/datarumah.csv', encoding='ISO-8859-1')

# Mengakses kolom data
bedrooms = data["KT"]
bathrooms = data["KM"]
luas = data["LB"]
floor = data["LR"]
prices = data["HARGA"]

# Normalisasi harga (skala miliaran)
prices_normalized = prices / 1_000_000_000  # Harga dalam skala miliar

# Menggabungkan fitur
features = np.array(list(zip(bedrooms, bathrooms, luas, floor)))

# Normalisasi fitur dengan StandardScaler
scaler = StandardScaler()
features_scaled = scaler.fit_transform(features)

# Mengubah harga ke bentuk array
prices_normalized = np.array(prices_normalized).reshape(-1, 1)

# Membuat model TensorFlow (jika belum ada)
model = tf.keras.Sequential([
    tf.keras.layers.Dense(units=10, activation='relu', input_shape=[4]),
    tf.keras.layers.Dense(units=1)
])

model.compile(optimizer='adam', loss='mse', metrics=['mae'])

# Mengecek apakah model sudah dilatih sebelumnya
if os.path.exists(model_weights_path):
    print("Model ditemukan, memuat bobot...")
    model.load_weights(model_weights_path)
else:
    print("Model tidak ditemukan, melatih ulang...")
    model.fit(features_scaled, prices_normalized, epochs=1000, batch_size=32, verbose=0)  # Melatih model
    model.save_weights(model_weights_path)
    print("Model dilatih dan bobot disimpan.")

# Membaca input dari argumen sistem
try:
    kamar_tidur = int(sys.argv[1])
    kamar_mandi = int(sys.argv[2])
    luas_rumah = int(sys.argv[3])
    jumlah_lantai = int(sys.argv[4])
except (IndexError, ValueError):
    print("Error: Pastikan semua argumen input telah dimasukkan dengan benar.")
    sys.exit(1)

# Melakukan prediksi
input_data = np.array([[kamar_tidur, kamar_mandi, luas_rumah, jumlah_lantai]])

# Normalisasi input yang dimasukkan
input_scaled = scaler.transform(input_data)

# Prediksi harga
prediksi_harga = model.predict(input_scaled)

# Denormalisasi hasil prediksi
prediksi_harga_denormalized = prediksi_harga * 1_000_000_000

# Output hasil prediksi
print(f"Rp. {prediksi_harga_denormalized[0][0]:,.0f}")  # Hasil prediksi dalam format yang diinginkan