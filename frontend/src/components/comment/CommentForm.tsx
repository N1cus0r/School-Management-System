import {FormType} from "../../entities/FormType.ts";
import {FormikProps} from "formik";
import {Button, FormControl, FormLabel, Stack, Text, Textarea,} from "@chakra-ui/react";
import CourseStudentSelect from "../shared/CourseStudentSelect.tsx";

interface Props {
  formType: FormType;
  form: FormikProps<{
    text: string;
    studentId: number;
  }>;
  submitButtonText: string;
}
const CommentForm = ({ formType, form, submitButtonText }: Props) => {
  return (
    <Stack spacing={4}>
      <FormControl id="text">
        <FormLabel>Text</FormLabel>
        <Textarea
          name="text"
          placeholder="Homework Description"
          rows={6}
          resize="none"
          value={form.values.text}
          onChange={form.handleChange}
        />
        {form.touched.text && form.errors.text && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.text}
          </Text>
        )}
      </FormControl>
      {formType === FormType.CREATE && (
        <FormControl>
          <FormLabel>Student</FormLabel>
          <CourseStudentSelect
            value={form.values.studentId}
            onChange={form.handleChange}
          />
        </FormControl>
      )}
      <Button
        colorScheme={"blue"}
        variant={"solid"}
        isDisabled={!form.dirty || !form.isValid}
        onClick={form.handleSubmit}
      >
        {submitButtonText}
      </Button>
    </Stack>
  );
};
export default CommentForm;
