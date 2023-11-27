import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import {useFormik} from "formik";
import * as Yup from "yup";
import useCreateComment from "./useCreateComment.ts";
import {CREATE_COMMENT_VALIDATION_SCHEMA} from "../../schemas/comment-schemas.ts";

const useCreateCommentForm = (courseId: string, onSuccess: () => void) => {
  const createComment = useCreateComment(
    [...CACHE_KEY_COURSES, courseId, "comments"],
    onSuccess,
  );

  return useFormik({
    initialValues: {
      text: "",
      studentId: 0,
    },
    validationSchema: Yup.object(CREATE_COMMENT_VALIDATION_SCHEMA),
    onSubmit: (values, { resetForm }) => {
      createComment.mutate({ ...values, courseId });
      resetForm();
    },
  });
};

export default useCreateCommentForm;
