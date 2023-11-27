import {Grade} from "../../entities/Grade.ts";
import {useFormik} from "formik";
import * as Yup from "yup";
import useUpdateGrade from "./useUpdateGrade.ts";
import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import {UPDATE_GRADE_VALIDATION_SCHEMA} from "../../schemas/grade-schemas.ts";

const useUpdateGradeForm = (
  courseId: string,
  grade: Grade,
  onSuccess: () => void,
) => {
  const updateGrade = useUpdateGrade(
    [...CACHE_KEY_COURSES, courseId, "grades"],
    onSuccess,
  );

  return useFormik({
    initialValues: { value: grade.value, text: grade.text },
    validationSchema: Yup.object(UPDATE_GRADE_VALIDATION_SCHEMA),
    onSubmit: (values) => updateGrade.mutate([grade.id, values]),
  });
};

export default useUpdateGradeForm;
