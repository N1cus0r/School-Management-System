import * as Yup from "yup";
import { useFormik } from "formik";
import { Homework } from "../../entities/Homework.ts";
import { HOMEWORK_VALIDATION_SCHEMA } from "../../schemas/homework-schemas.ts";
import useUpdateHomework from "./useUpdateHomework.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";

const useUpdateHomeworkForm = (
  courseId: string,
  homework: Homework,
  onSuccess: () => void,
) => {
  const updateHomework = useUpdateHomework(
    [...CACHE_KEY_COURSES, courseId, "homeworks"],
    onSuccess,
  );

  return useFormik({
    initialValues: { text: homework.text, dueDate: homework.dueDate },
    validationSchema: Yup.object(HOMEWORK_VALIDATION_SCHEMA),
    onSubmit: (values) => updateHomework.mutate([homework.id, values]),
  });
};

export default useUpdateHomeworkForm;
