package com.example.vamomarcarintegrado;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
    /**
     * Calcula o numero de colunas que cabem na tela ao usar o tipo de
     * visualizacao GRID no RecycleView
     *
     * @param context contexto utilizado, geralmente instancia da activity que
     *                contem o RecycleView
     * @param columnWidthDp largura do item do grid em dp
     * @return numero de colunas a serem usadas no GridLayoutManager
     */
    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    /**
     * Carrega um arquivo de imagem em um bitmap sem realizar qualquer tipo de escala.
     * @param imagePath caminho local de onde esta localizado o arquivo de imagem.
     * @return o bitmap.
     */
    public static Bitmap getBitmap(String imagePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }


    /**
     * Carrega um arquivo de imagem e aplica um fator de escala.
     * @param imagePath caminho local de onde esta localizado o arquivo de imagem
     * @param scaleFactor fator de escala a ser aplicado na imagem. Se
     *                    quiser uma imagem com metade do tamanho, deve
     *                    usar o valor 2 como entrada. Uma imagem com um
     *                    quarto do tamanho deve usar 4 como entrada.
     * @return o bitmap
     */
    public static Bitmap getBitmap(String imagePath, int scaleFactor) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    /**
     * Carrega um arquivo de imagem em um bitmap de tamanho definido.
     * @param imagePath caminho local de onde esta localizado o arquivo de imagem.
     * @param w largura que a imagem deve ter.
     * @param h altura que a imagem deve ter.
     * @return o bitmap
     */
    public static Bitmap getBitmap(String imagePath, int w, int h) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.max(photoW/w, photoH/h);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    /**
     * Carrega uma imagem em um bitmap a partir de um URI.
     * @param context contexto utilizado, geralmente instancia da activity que
     *                chama a função
     * @param imageLocation endereço URI da imagem
     * @return o bitmap
     * @throws FileNotFoundException caso a imagem não seja achada
     */
    public static Bitmap getBitmap(Context context, Uri imageLocation) throws FileNotFoundException {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 1;
        bmOptions.inPurgeable = true;
        InputStream is = context.getContentResolver().openInputStream(imageLocation);
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    /**
     * Carrega uma imagem em um bitmap a partir de um URI.
     * A imagem sofrerá escala de acordo com as dimensões escohidas.
     * @param context contexto utilizado, geralmente instancia da activity que
     *                chama a função
     * @param imageLocation endereço URI da imagem
     * @param scaleFactor fator de escala a ser aplicado na imagem. Se
     *                    quiser uma imagem com metade do tamanho, deve
     *                    usar o valor 2 como entrada. Uma imagem com um
     *                    quarto do tamanho deve usar 4 como entrada.
     * @return o bitmap
     * @throws FileNotFoundException caso a imagem não seja achada
     */
    public static Bitmap getBitmap(Context context, Uri imageLocation, int scaleFactor) throws FileNotFoundException {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        InputStream is = context.getContentResolver().openInputStream(imageLocation);
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    /**
     * Carrega uma imagem em um bitmap a partir de um URI.
     * A imagem sofrerá escala de acordo com as dimensões escohidas.
     * @param context contexto utilizado, geralmente instancia da activity que
     *                chama a função
     * @param imageLocation endereço URI da imagem
     * @param w largura que a imagem deve ter
     * @param h altura que a imagem deve ter
     * @return o bitmap
     * @throws FileNotFoundException caso a imagem não seja achada
     */
    public static Bitmap getBitmap(Context context, Uri imageLocation, int w, int h) throws FileNotFoundException {

        InputStream is = context.getContentResolver().openInputStream(imageLocation);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/w, photoH/h);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        is = context.getContentResolver().openInputStream(imageLocation);
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    /**
     * Aplica uma escala em um arquivo de imagem já existente.
     * @param imageLocation caminho absoluto onde esta salva a imagem
     * @param scaleFactor fator de escala a ser aplicado na imagem. Se
     *                    quiser uma imagem com metade do tamanho, deve
     *                    usar o valor 2 como entrada. Uma imagem com um
     *                    quarto do tamanho deve usar 4 como entrada.
     * @throws FileNotFoundException se o arquivo nao existe
     */
    public static void scaleImage(String imageLocation, int scaleFactor) throws FileNotFoundException {
        // Decode the image file into a Bitmap sized to fill the View
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bmp =  BitmapFactory.decodeFile(imageLocation, bmOptions);

        FileOutputStream out = new FileOutputStream(imageLocation);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
    }

    /**
     * Salva um Bitmap em um arquivo.
     * @param bmp bitmap a ser salvo
     * @param imageLocation caminho absoluto onde esta salva a imagem
     * @throws FileNotFoundException se o arquivo nao existe
     */
    public static void saveImage(Bitmap bmp, String imageLocation) throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream(imageLocation);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
    }

    /**
     * Converte um InputStream para uma String.
     * @param is InputStream a ser convertido.
     * @param charset o charset a ser utilizado na conversão.
     * @return a string.
     * @throws FileNotFoundException se o arquivo nao existe
     */
    public static String inputStream2String(InputStream is, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                is, charset), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }

    /**
     * Converte um String em formato Base64 para Bitmap
     * @param myImageData string Base64
     * @return o Bitmap.
     * @throws FileNotFoundException se o arquivo nao existe
     */
    public static Bitmap base642Bitmap(String myImageData)
    {
        byte[] imageAsBytes = Base64.decode(myImageData.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * Método que deve ser chamado para realizar a formatação
     *
     * @param ediTxt
     * @param mask
     * @return
     */
    public static TextWatcher mask(final EditText ediTxt, final String mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {}

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = Util.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (final Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
        };
    }


    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "");
    }
}
