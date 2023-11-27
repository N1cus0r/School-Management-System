import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import {useFormik} from "formik";
import * as Yup from "yup";
import {UPDATE_COMMENT_VALIDATION_SCHEMA} from "../../schemas/comment-schemas.ts";
import {Comment} from "../../entities/Comment.ts";
import useUpdateComment from "./useUpdateComment.ts";

const useUpdateCommentForm = (
  courseId: string,
  comment: Comment,
  onSuccess: () => void,
) => {
  const updateComment = useUpdateComment(
    [...CACHE_KEY_COURSES, courseId, "comments"],
    onSuccess,
  );

  return useFormik({
    initialValues: { text: comment.text },
    validationSchema: Yup.object(UPDATE_COMMENT_VALIDATION_SCHEMA),
    onSubmit: (values) => updateComment.mutate([comment.id, values]),
  });
};

export default useUpdateCommentForm;
