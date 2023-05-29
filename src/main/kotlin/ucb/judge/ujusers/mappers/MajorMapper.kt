package ucb.judge.ujusers.mappers

import ucb.judge.ujusers.dao.Major
import ucb.judge.ujusers.dto.MajorDto

class MajorMapper {
    companion object {
        fun entityToDto(major: Major): MajorDto {
            return MajorDto(
                majorId = major.majorId,
                name = major.name
            )
        }
    }
}
