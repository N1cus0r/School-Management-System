import { CACHE_KEY_COURSES } from "../../utils/constants.ts";
import useCreateGrade from "./useCreateGrade.ts";
import { useFormik } from "formik";
import * as Yup from "yup";
import { CREATE_GRADE_VALIDATION_SCHEMA } from "../../schemas/grade-schemas.ts";

const useCreateGradeForm = (courseId: string, onSuccess: () => void) => {
  const createGrade = useCreateGrade(
    [...CACHE_KEY_COURSES, courseId, "grades"],
    onSuccess,
  );

  return useFormik({
    initialValues: { value: 0, text: "", studentId: 0 },
    validationSchema: Yup.object(CREATE_GRADE_VALIDATION_SCHEMA),
    onSubmit: (values, { resetForm }) => {
      createGrade.mutate({ ...values, courseId });
      resetForm();
    },
  });
};

export default useCreateGradeForm;
