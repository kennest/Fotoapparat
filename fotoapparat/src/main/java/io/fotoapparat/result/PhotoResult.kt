package io.fotoapparat.result

import android.graphics.Bitmap
import io.fotoapparat.exif.ExifWriter
import io.fotoapparat.log.Logger
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.result.transformer.BitmapPhotoTransformer
import io.fotoapparat.result.transformer.SaveToFileTransformer
import io.fotoapparat.result.transformer.originalResolution
import java.io.File
import java.util.concurrent.Future

/**
 * Result of taking the photo.
 */
class PhotoResult internal constructor(private val pendingResult: PendingResult<Photo>) {

    /**
     * Converts result to [Bitmap] of size provided by the [sizeTransformer].
     *
     * @param sizeTransformer Given the original size of the photo, returns the updated size so that
     * photo will be downscaled, upscaled or unchanged.
     * @return result as pending [BitmapPhoto] which will be available at some point in the
     * future.
     */
    @JvmOverloads
    fun toBitmap(sizeTransformer: (Resolution) -> Resolution = originalResolution()): PendingResult<BitmapPhoto> {
        return pendingResult.transform(BitmapPhotoTransformer(sizeTransformer))
    }

    /**
     * Saves result to file.
     *
     * @return pending operation which completes when photo is saved to file.
     */
    fun saveToFile(file: File): PendingResult<Unit> {
        return pendingResult.transform(SaveToFileTransformer(
                file = file,
                exifOrientationWriter = ExifWriter
        ))
    }

    /**
     * @return result as [PendingResult].
     */
    fun toPendingResult(): PendingResult<Photo> {
        return pendingResult
    }

    companion object {

        /**
         * Creates a new instance of advanced result from a Future result.
         *
         * @param photoFuture The future result of a [Photo].
         * @return The result.
         */
        internal fun fromFuture(
                photoFuture: Future<Photo>,
                logger: Logger
        ) = PhotoResult(
                PendingResult.fromFuture(photoFuture, logger)
        )

    }

}