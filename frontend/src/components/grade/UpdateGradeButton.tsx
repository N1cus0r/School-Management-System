import { Grade } from "../../entities/Grade.ts";
import EditButton from "../shared/EditButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import { useDisclosure } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import useUpdateGradeForm from "../../hooks/grade/useUpdateGradeForm.ts";
import GradeForm from "./GradeForm.tsx";
import { FormType } from "../../entities/FormType.ts";

interface Props {
  grade: Grade;
}

const UpdateGradeButton = ({ grade }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const updateGradeForm = useUpdateGradeForm(courseId!, grade, onClose);

  return (
    <>
      <EditButton onClick={onOpen} />
      <CustomDrawer isOpen={isOpen} onClose={onClose} heading={"Update Grade"}>
        <GradeForm
          formType={FormType.UPDATE}
          form={updateGradeForm}
          submitButtonText={"Edit"}
        />
      </CustomDrawer>
    </>
  );
};
export default UpdateGradeButton;
