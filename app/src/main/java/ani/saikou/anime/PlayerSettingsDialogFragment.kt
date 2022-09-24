package ani.saikou.anime

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import ani.saikou.*
import ani.saikou.databinding.BottomSheetCurrentPlayerSettingsBinding
import ani.saikou.media.Media
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class PlayerSettingsDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetCurrentPlayerSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetCurrentPlayerSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as ExoplayerView
        val settings = activity.settings
        val player = "player_settings"

        //Hide System UI
        @Suppress("DEPRECATION")
        binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        //Video
        binding.playerSettingsVideoInfo.isChecked = settings.videoInfo
        binding.playerSettingsVideoInfo.setOnCheckedChangeListener { _, isChecked ->
            settings.videoInfo = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsQualityHeight.setText((loadData<Int>("maxHeight", toast = false) ?: 480).toString())
        binding.playerSettingsQualityHeight.addTextChangedListener {
            val height = binding.playerSettingsQualityHeight.text.toString().toIntOrNull()
            saveData("maxHeight", height)
        }
        binding.playerSettingsQualityWidth.setText((loadData<Int>("maxWidth", toast = false) ?: 720).toString())
        binding.playerSettingsQualityWidth.addTextChangedListener {
            val height = binding.playerSettingsQualityWidth.text.toString().toIntOrNull()
            saveData("maxWidth", height)
        }


        val speeds = arrayOf(0.25f, 0.33f, 0.5f, 0.66f, 0.75f, 1f, 1.25f, 1.33f, 1.5f, 1.66f, 1.75f, 2f)
        val cursedSpeeds = arrayOf(1f, 1.25f, 1.5f, 1.75f, 2f, 2.5f, 3f, 4f, 5f, 10f, 25f, 50f)
        var curSpeedArr = if (settings.cursedSpeeds) cursedSpeeds else speeds
        var speedsName = curSpeedArr.map { "${it}x" }.toTypedArray()
        binding.playerSettingsSpeed.text = getString(R.string.default_playback_speed, speedsName[settings.defaultSpeed])
        val speedDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Default Speed")
        binding.playerSettingsSpeed.setOnClickListener {
            speedDialog.setSingleChoiceItems(speedsName, settings.defaultSpeed) { dialog, i ->
                settings.defaultSpeed = i
                binding.playerSettingsSpeed.text = getString(R.string.default_playback_speed, speedsName[i])
                saveData(player, settings)
                dialog.dismiss()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        binding.playerSettingsCursedSpeeds.isChecked = settings.cursedSpeeds
        binding.playerSettingsCursedSpeeds.setOnCheckedChangeListener { _, isChecked ->
            settings.cursedSpeeds = isChecked
            curSpeedArr = if (settings.cursedSpeeds) cursedSpeeds else speeds
            settings.defaultSpeed = if (settings.cursedSpeeds) 0 else 5
            speedsName = curSpeedArr.map { "${it}x" }.toTypedArray()
            binding.playerSettingsSpeed.text = getString(R.string.default_playback_speed, speedsName[settings.defaultSpeed])
            saveData(player, settings)
        }

        //Time Stamp
        binding.playerSettingsTimeStamps.isChecked = settings.timeStampsEnabled
        binding.playerSettingsTimeStamps.setOnCheckedChangeListener { _, isChecked ->
            settings.timeStampsEnabled = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsShowTimeStamp.isChecked = settings.showTimeStampButton
        binding.playerSettingsShowTimeStamp.setOnCheckedChangeListener { _, isChecked ->
            settings.showTimeStampButton = isChecked
            saveData(player, settings)
        }


        //Auto
        binding.playerSettingsAutoSkipOpEd.isChecked = settings.autoSkipOPED
        binding.playerSettingsAutoSkipOpEd.setOnCheckedChangeListener { _, isChecked ->
            settings.autoSkipOPED = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsAutoPlay.isChecked = settings.autoPlay
        binding.playerSettingsAutoPlay.setOnCheckedChangeListener { _, isChecked ->
            settings.autoPlay = isChecked
            saveData(player, settings)
        }
        binding.playerSettingsAutoSkip.isChecked = settings.autoSkipFiller
        binding.playerSettingsAutoSkip.setOnCheckedChangeListener { _, isChecked ->
            settings.autoSkipFiller = isChecked
            saveData(player, settings)
        }

        //Update Progress
        binding.playerSettingsAskUpdateProgress.isChecked = settings.askIndividual
        binding.playerSettingsAskUpdateProgress.setOnCheckedChangeListener { _, isChecked ->
            settings.askIndividual = isChecked
            saveData(player, settings)
        }
        binding.playerSettingsAskUpdateHentai.isChecked = settings.updateForH
        binding.playerSettingsAskUpdateHentai.setOnCheckedChangeListener { _, isChecked ->
            settings.updateForH = isChecked
            if (isChecked) toastString(getString(R.string.very_bold))
            saveData(player, settings)
        }
        binding.playerSettingsCompletePercentage.value = (settings.watchPercentage * 100).roundToInt().toFloat()
        binding.playerSettingsCompletePercentage.addOnChangeListener { _, value, _ ->
            settings.watchPercentage = value / 100
            saveData(player, settings)
        }

        //Behaviour
        binding.playerSettingsAlwaysContinue.isChecked = settings.alwaysContinue
        binding.playerSettingsAlwaysContinue.setOnCheckedChangeListener { _, isChecked ->
            settings.alwaysContinue = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsPauseVideo.isChecked = settings.focusPause
        binding.playerSettingsPauseVideo.setOnCheckedChangeListener { _, isChecked ->
            settings.focusPause = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsVerticalGestures.isChecked = settings.gestures
        binding.playerSettingsVerticalGestures.setOnCheckedChangeListener { _, isChecked ->
            settings.gestures = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsDoubleTap.isChecked = settings.doubleTap
        binding.playerSettingsDoubleTap.setOnCheckedChangeListener { _, isChecked ->
            settings.doubleTap = isChecked
            saveData(player, settings)
        }

        binding.playerSettingsSeekTime.value = settings.seekTime.toFloat()
        binding.playerSettingsSeekTime.addOnChangeListener { _, value, _ ->
            settings.seekTime = value.toInt()
            saveData(player, settings)
        }

        binding.exoSkipTime.setText(settings.skipTime.toString())
        binding.exoSkipTime.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.exoSkipTime.clearFocus()
            }
            false
        }
        binding.exoSkipTime.addTextChangedListener {
            val time = binding.exoSkipTime.text.toString().toIntOrNull()
            if (time != null) {
                settings.skipTime = time
                saveData(player, settings)
            }
        }

        //Other
        binding.playerSettingsPiP.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                visibility = View.VISIBLE
                isChecked = settings.pip
                setOnCheckedChangeListener { _, isChecked ->
                    settings.pip = isChecked
                    saveData(player, settings)
                }
            } else visibility = View.GONE
        }

        binding.playerSettingsCast.isChecked = settings.cast
        binding.playerSettingsCast.setOnCheckedChangeListener { _, isChecked ->
            settings.cast = isChecked
            saveData(player, settings)
        }

        val resizeModes = arrayOf("Original", "Zoom", "Stretch")
        val resizeDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Default Resize Mode")
        binding.playerResizeMode.setOnClickListener {
            resizeDialog.setSingleChoiceItems(resizeModes, settings.resize) { dialog, count ->
                settings.resize = count
                saveData(player, settings)
                dialog.dismiss()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        fun restartApp() {
            Snackbar.make(
                binding.root,
                R.string.restart_app, Snackbar.LENGTH_SHORT
            ).apply {
                val mainIntent =
                    Intent.makeRestartActivityTask(context.packageManager.getLaunchIntentForPackage(context.packageName)!!.component)
                setAction("Do it!") {
                    context.startActivity(mainIntent)
                    Runtime.getRuntime().exit(0)
                }
                show()
            }
        }

        fun toggleButton(button: android.widget.Button, toggle: Boolean) {
            button.isClickable = toggle
            button.alpha = when (toggle) {
                true  -> 1f
                false -> 0.5f
            }
        }

        fun toggleSubOptions(isChecked: Boolean) {
            toggleButton(binding.videoSubColorPrimary, isChecked)
            toggleButton(binding.videoSubColorSecondary, isChecked)
            toggleButton(binding.videoSubOutline, isChecked)
            toggleButton(binding.videoSubFont, isChecked)
            toggleButton(binding.subLang, isChecked)
            binding.subtitleFontSizeCard.isEnabled = isChecked
            binding.subtitleFontSizeCard.isClickable = isChecked
            binding.subtitleFontSizeCard.alpha = when (isChecked) {
                true  -> 1f
                false -> 0.5f
            }
            binding.subtitleFontSize.isEnabled = isChecked
            binding.subtitleFontSize.isClickable = isChecked
            binding.subtitleFontSize.alpha = when (isChecked) {
                true  -> 1f
                false -> 0.5f
            }
            binding.subtitleFontSizeText.isEnabled = isChecked
            binding.subtitleFontSizeText.isClickable = isChecked
            binding.subtitleFontSizeText.alpha = when (isChecked) {
                true  -> 1f
                false -> 0.5f
            }
        }
        binding.subSwitch.isChecked = settings.subtitles
        binding.subSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.subtitles = isChecked
            saveData(player, settings)
            toggleSubOptions(isChecked)
            restartApp()
        }
        val colorsPrimary =
            arrayOf("Black", "Dark Gray", "Gray", "Light Gray", "White", "Red", "Yellow", "Green", "Cyan", "Blue", "Magenta")
        val primaryColorDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Primary Sub Color")
        binding.videoSubColorPrimary.setOnClickListener {
            primaryColorDialog.setSingleChoiceItems(colorsPrimary, settings.primaryColor) { dialog, count1 ->
                settings.primaryColor = count1
                saveData(player, settings)
                dialog.dismiss()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        val colorsSecondary = arrayOf(
            "Black",
            "Dark Gray",
            "Gray",
            "Light Gray",
            "White",
            "Red",
            "Yellow",
            "Green",
            "Cyan",
            "Blue",
            "Magenta",
            "Transparent"
        )
        val secondaryColorDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Outline Sub Color")
        binding.videoSubColorSecondary.setOnClickListener {
            secondaryColorDialog.setSingleChoiceItems(colorsSecondary, settings.secondaryColor) { dialog, count2 ->
                settings.secondaryColor = count2
                saveData(player, settings)
                dialog.dismiss()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        val typesOutline = arrayOf("Outline", "Shine", "Drop Shadow", "None")
        val outlineDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Outline Type")
        binding.videoSubOutline.setOnClickListener {
            outlineDialog.setSingleChoiceItems(typesOutline, settings.outline) { dialog, count3 ->
                settings.outline = count3
                saveData(player, settings)
                dialog.dismiss()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        val fonts = arrayOf("Poppins Semi Bold", "Poppins Bold", "Poppins", "Poppins Thin")
        val fontDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Subtitle Font")
        binding.videoSubFont.setOnClickListener {
            fontDialog.setSingleChoiceItems(fonts, settings.font) { dialog, count4 ->
                settings.font = count4
                saveData(player, settings)
                dialog.dismiss()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        val locales = arrayOf(
            "Default (en-US)",
            "[ja-JP] Japanese",
            "[en-US] English",
            "[de-DE] German",
            "[es-419] Spanish",
            "[es-ES] Spanish (Spain)",
            "[fr-FR] French",
            "[it-IT] Italian",
            "[ar-SA] Arabic (Saudi Arabia)",
            "[ar-ME] Arabic (Montenegro)",
            "[pt-BR] Portuguese (Brazil)",
            "[pt-PT] Portuguese (Portugal)",
            "[ru-RU] Russian",
            "[zh-CN] Chinese",
            "[tr-TR] Turkish"
        )
        val localeDialog = AlertDialog.Builder(activity, R.style.DialogTheme).setTitle("Subtitle Language")
        binding.subLang.setOnClickListener {
            localeDialog.setSingleChoiceItems(locales, settings.locale) { dialog, count5 ->
                settings.locale = count5
                saveData(player, settings)
                dialog.dismiss()
                restartApp()
            }.show().window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        binding.subtitleFontSize.setText(settings.fontSize.toString())
        binding.subtitleFontSize.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.subtitleFontSize.clearFocus()
            }
            false
        }
        binding.subtitleFontSize.addTextChangedListener {
            val size = binding.subtitleFontSize.text.toString().toIntOrNull()
            if (size != null) {
                settings.fontSize = size
                saveData(player, settings)
            }
        }
        toggleSubOptions(settings.subtitles)
        }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val activity = requireActivity() as ExoplayerView
        val model = activity.model
        val episode: Episode? = null

        fun stopAddingToList() {
            episode?.extractorCallback = null
            episode?.also {
                it.extractors = it.extractors?.toMutableList()
            }
        }
        fun startExoplayer(media: Media) {
            dismiss()
                stopAddingToList()
                val intent = Intent(activity, ExoplayerView::class.java).apply {
                    putExtra("media", media)
                }
                startActivity(intent)
        }
        model.getMedia().observe(viewLifecycleOwner) { m ->
            activity.changingServer = true
            m.selected!!.server = null
            model.saveSelected(m.id, m.selected!!, activity)
            startExoplayer(m)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object{
        fun newInstance() = PlayerSettingsDialogFragment()
    }
}