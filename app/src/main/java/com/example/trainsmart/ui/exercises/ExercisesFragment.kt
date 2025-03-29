package com.example.trainsmart.ui.exercises

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainsmart.R
import com.example.trainsmart.data.Exercise
import com.example.trainsmart.data.User
import com.example.trainsmart.databinding.FragmentExercisesBinding
import com.example.trainsmart.firestore.FireStoreClient
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ExercisesViewModel::class.java)

        _binding = FragmentExercisesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val exerciseList: RecyclerView = root.findViewById(R.id.exercise_list)

        val firestoreClient = FireStoreClient()

        var exercises = mutableListOf<Exercise>()

        val exerciseModels = mutableListOf<ExerciseListItemModel>()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Упражнения"

        lifecycleScope.launch {
            firestoreClient.getAllExercises().collect { result ->
                // Start Animation


                if (result.isNotEmpty()) {
                    println(909)
                    for (exercise in result)
                        exercise?.let { exercises.add(it)
                            println(exercise.name)
                            println(exercises.size)
                        }
                    for (exercise in exercises) {
                        //println(133)
                        //println(exercises.size)
                        //println(exercise.name)
                        exerciseModels.add(
                            ExerciseListItemModel(
                                "",
                                exercise.name,
                                R.drawable.exercise4,
                                exercise.description,
                                exercise.technique,
                                "3 подхода по 6 повторений"
                            )
                        )
                        //exerciseListAdapter.notifyDataSetChanged()
                    }
                }
                else{
                    println("result is null")
                }

                println("SIZE:")
                println(exerciseModels.size)

                var exerciseListAdapter = ExerciseListAdapter(requireContext(), exerciseModels, { exercise ->
                    var arguments = Bundle().apply {
                        putString("exerciseName", exercise.name)
                        putInt("exercisePhoto", exercise.photo)
                        putString("exerciseDescription", exercise.description)
                        putString("exerciseTechnique", exercise.technique)
                    }
                    findNavController().navigate(R.id.navigation_exercise_details, arguments)
                })
                exerciseListAdapter.notifyDataSetChanged()
                exerciseList.setAdapter(exerciseListAdapter)
                exerciseList.layoutManager = LinearLayoutManager(context)

                // Finish Animation

                val searchBox: EditText = root.findViewById(R.id.exercise_search)
                searchBox.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        exerciseListAdapter.setFilter(s.toString())
                    }
                })
            }


        }


        //exerciseListAdapter.notifyDataSetChanged()
        //exerciseListAdapter.setFilter(" ")


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Упражнения"
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Упражнения"

    }

    override fun onDestroyView() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Упражнения"

        super.onDestroyView()
        _binding = null
    }

}

//        val exerciseModels = ArrayList<ExerciseListItemModel>()
//
//        for (exercise in exercises) {
//            exerciseModels.add(
//                ExerciseListItemModel(
//                exercise.name, R.drawable.exercise1,
//                exercise.description, exercise.technique, "3 подхода по 6 повторений"
//            ))
//        }

// Сюда
//        exerciseModels.add(ExerciseListItemModel(
//            "Жим лёжа", R.drawable.exercise1,
//            "Базовое упражнение, которое помогает развить мышцы груди, плеч и рук.",
//            "1. Штанга находится на уровне глаз" +
//                    "\n2. Занять такое положение на скамье, чтобы голова, поясница, лопатки и ноги имели точки опоры." +
//                    "\n3. Чтобы избежать травм, во время выполнения упражнения" +
//                    "лопатки должны быть сведены." +
//                    "\n4. Опускание штанги происходит на вдохе, подъем — на выдохе." +
//                    "\n5. Ваш хват должен быть сверху, большие пальцы должны располагаться под штангой и поверх пальцев.",
//            "3 подхода по 6 повторений"
//                    ))
//        exerciseModels.add(ExerciseListItemModel(
//            "Становая тяга", R.drawable.exercise2,
//            "Базовое упражнение силового тренинга, которое заключается в подъеме штанги с "+
//                    "пола за счет мышц ног и спины.",
//            "1. Располагаем ноги на ширине плеч. Стопы параллельны друг другу. Штанга" +
//                    "соприкасается с голенью ноги." +
//                    "\n2. Держим прямую спину с лёгким прогибом во время всего упражнения.  " +
//                    "Лопатки опущены вниз и сведены вместе. Грудь выводим вверх. Голова смотрит прямо, шея ровная." +
//                     "\n3. На вдохе отводим таз и сгибаем колени, наклоняя корпус вперёд, чтобы взять " +
//                    "штангу руками на ширине плеч или чуть шире. " +
//                    "\n4. В положении полуприседа держим руками штангу. Плечи не рекомендуется выводить" +
//                            " сильно вперёд за снаряд." +
//                    "\n5. На выдохе мощным движением тянем штангу руками вверх. Поднимаем таз и разгибаемся в коленях." +
//                    "\n6. Тянем штангу до полного выпрямления корпуса и ног.",
//            "3 подхода по 8 повторений "
//        ))
//        exerciseModels.add(ExerciseListItemModel(
//            "Тяга вертикального блока", R.drawable.exercise3_old,
//            "Одно из фундаментальных упражнений для развития верхней части туловища",
//            "1. Сядьте на скамью тренажёра, согните колени под прямым углом, зафиксируйте" +
//                    "бёдра под подушкой, настроенной под высоту голени. Держите корпус вертикально." +
//                    "Живот собран и немного втянут (не выпячивается). Такая позиция даёт вам прочную" +
//                    "опору, которая поможет избежать раскачиваний корпуса и потери силы при движении." +
//                    "\n2. Возьмите рукоять прямым хватом на расстоянии полторы ширины плеч, локти чуть согнуты." +
//                    "\n3. Для начала движения на выдохе выгните корпус в грудном отделе как во время" +
//                    "подтягиваний: плечи отведите назад, максимально сведите лопатки, согните локти" +
//                    "и подтяните рукоятку тренажёра до шеи, почти касаясь верха грудного отдела." +
//                    "\n4. Доведя рукоять до конца, на долю секунды зафиксируйте лопатки сведёнными," +
//                    "а плечи — отведёнными назад. Затем плавно на вдохе отпустите рукоять вверх — в" +
//                    "стартовое положение.",
//            "2 подхода по 10 повторений"
//        ))
//        exerciseModels.add(ExerciseListItemModel(
//            "Приседания со штангой", R.drawable.exercise4,
//            "Эффективное базовое упражнение, которое позволяет увеличить силу, мощность " +
//                    "и мышечную массу нижней части тела.",
//            "1. Плотно зафиксируйте ладони на грифе, коленные суставы немного согните и " +
//                    "сделайте один шаг вперед. Пройдя под снарядом, поставьте обе ноги на одном " +
//                    "уровне и поднимитесь, следя за тем, чтобы лопатки оказались сведенными, а гриф" +
//                    "лег на верхнюю часть спины. В верном положении вес ложится на трапецию и " +
//                    "поддерживается задними дельтовидными мышцами. Если ощутите слишком сильное" +
//                    "болезненное давление на позвоночник, вернитесь в начальную позицию и повторите подход." +
//                    "\n2. Теперь выпрямите ноги и снимите штангу с опоры. Следом шагните назад, " +
//                    "голову держите прямо, чтобы было проще держать прогиб в пояснице и, опять же," +
//                    "не потерять равновесие. Стопы расставьте шире плеч, немного разводя носки в стороны." +
//                    "\n3. Убедившись, что лопатки сведены, спина находится в легком наклоне вперед, " +
//                    "а мышц пресса напряжены, на выдохе плавно разведите и согните колени. Не " +
//                    "отводите таз назад, словно садитесь на воображаемый стул (распространенная ошибка" +
//                    "среди начинающих атлетов). Голени и колени должны работать в естественной для тела плоскости." +
//                    "\n4. Приседайте достаточно глубоко, чтобы тазовые кости оказались ниже коленных суставов." +
//                    "Так нагрузка будет распределяться равномерно, суставы со связками не испытают перенапряжения." +
//                    "\n5. Из нижней позиции толкните тело вверх, плавно разгибая колени. Избегайте " +
//                    "резких движений, следите за техникой и ни в коем случае не гонитесь за скоростью." +
//                    "\n6. В завершении нужного числа повторов подойдите к стойке и верните снаряд " +
//                    "на место. Делать это нужно, слегка сгибая коленные суставы, но не наклоняясь.",
//            "4 подхода по 5 повторений"
//        ))
//        exerciseModels.add(ExerciseListItemModel(
//            "Тяга горизонтального блока", R.drawable.exercise5,
//            "Cиловое упражнение на развитие мышц спины, которое выполняется на блочном" +
//                    "тренажёре. Его также называют тягой нижнего блока к поясу и просто тягой к животу.",
//            "1. Расположитесь на сидении тренажёра, упираясь стопами ног в пол или в " +
//                    "специальную платформу. Ноги слегка согнуты в коленях. Возьмитесь за рукоятку, "+
//                    "предварительно подобрав её для себя." +
//                    "\n2. Спина прямая с лёгким прогибом в пояснице, лопатки опущены вниз и сведены" +
//                            "вместе, грудь выведена вперёд, шея ровная, взгляд направлен прямо." +
//                    "\n3. На выдохе подтяните рукоятку к животу. На выдохе сгибайте руки в локтях, " +
//                    "отводя прижатые локти к корпусу за спину. Сделайте небольшую паузу." +
//                    "\n4. На вдохе медленно выпрямите руки в локтевом суставе. В упражнении " +
//                    "допускается небольшое маятниковое раскачивание корпуса вперёд и назад." +
//                    "\n5. Постарайтесь держать спину ровной на протяжении всего упражнения, а лопатки собранными вместе.",
//            "3 подхода по 11 повторений"
//        ))
//        exerciseModels.add(ExerciseListItemModel(
//            "Подъем штанги на бицепс", R.drawable.exercise6,
//            "Cиловое изолированное упражнение, направленное на развитие бицепса плеча.",
//            "1. Встаньте ровно со штангой в руках, спина прямая, ноги на ширине плеч." +
//                    "\n2. Отрегулируйте хват так, чтобы было удобно — на ширине плеч или у́же." +
//                    "Опустите плечи вниз и сведите лопатки вместе. Это придаст корпусу больше " +
//                    "стабильности и жёсткости." +
//                    "\n3. Прижмите локти к телу и удерживайте их в этом положении, чтобы " +
//                    "исключить из работы дельтовидные мускулы." +
//                    "\n4. Одновременно делайте выдох и поднимайте штангу вверх до груди," +
//                    "сгибая руки в локтевых суставах. Сделайте небольшую паузу в верхней точке." +
//                    "\n5. На выдохе медленно и подконтрольно опускайте штангу в нижнюю точку," +
//                    "разгибая руки в локтях.",
//            "5 подходов по 5 раз"
//        ))