import { useFormik } from "formik";
import * as Yup from "yup";
import { HOMEWORK_VALIDATION_SCHEMA } from "../../schemas/homework-schemas.ts";
import useCreateHomework from "./useCreateHomework.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";

const useCreateHomeworkForm = (courseId: string, onSuccess: () => void) => {
  const createHomework = useCreateHomework(
    [...CACHE_KEY_COURSES, courseId, "homeworks"],
    onSuccess,
  );

  return useFormik({
    initialValues: { text: "", dueDate: "" },
    validationSchema: Yup.object(HOMEWORK_VALIDATION_SCHEMA),
    onSubmit: (values, { resetForm }) => {
      createHomework.mutate({ ...values, courseId });
      resetForm();
    },
  });
};
export default useCreateHomeworkForm;
