package com.mdau.proelitecars.vehicle.service;

import com.mdau.proelitecars.cloudinary.service.CloudinaryService;
import com.mdau.proelitecars.common.exception.ResourceNotFoundException;
import com.mdau.proelitecars.vehicle.dto.*;
import com.mdau.proelitecars.vehicle.entity.*;
import com.mdau.proelitecars.vehicle.repository.VehicleRepository;
import com.mdau.proelitecars.vehicle.repository.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final CloudinaryService cloudinaryService;

    // ── Public: paginated list with filters ───────────────────────────────
    @Transactional(readOnly = true)
    public Page<VehicleCardDto> findAll(
            String make, String model,
            Integer minYear, Integer maxYear,
            BigDecimal minPrice, BigDecimal maxPrice,
            Integer minMileage, Integer maxMileage,
            VehicleCondition condition, BodyStyle bodyStyle,
            FuelType fuelType, Transmission transmission,
            String location, VehicleStatus status,
            VehicleBadge badge, String keyword,
            Pageable pageable) {

        Specification<Vehicle> spec = VehicleSpecification.withFilters(
                make, model, minYear, maxYear,
                minPrice, maxPrice, minMileage, maxMileage,
                condition, bodyStyle, fuelType, transmission,
                location, status, badge, keyword);

        return vehicleRepository.findAll(spec, pageable)
                .map(vehicleMapper::toCardDto);
    }

    // ── Public: featured vehicles ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<VehicleCardDto> findFeatured() {
        return vehicleRepository
                .findTop6ByFeaturedTrueAndStatusOrderByCreatedAtDesc(
                        VehicleStatus.AVAILABLE)
                .stream()
                .map(vehicleMapper::toCardDto)
                .toList();
    }

    // ── Public: single vehicle ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public VehicleDto findById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", id));
        return vehicleMapper.toDto(vehicle);
    }

    // ── Public: distinct makes from live inventory ────────────────────────
    @Transactional(readOnly = true)
    public List<String> findDistinctMakes() {
        return vehicleRepository.findDistinctMakes();
    }

    // ── Public: distinct models for a given make ──────────────────────────
    @Transactional(readOnly = true)
    public List<String> findDistinctModelsByMake(String make) {
        return vehicleRepository.findDistinctModelsByMake(make);
    }

    // ── Public: search metadata (makes, styles, years, price/mileage range)
    @Transactional(readOnly = true)
    public VehicleSearchMetaDto getSearchMeta() {
        List<String> makes      = vehicleRepository.findDistinctMakes();
        List<String> bodyStyles = vehicleRepository.findDistinctBodyStyles();
        List<Integer> years     = vehicleRepository.findDistinctYears();
        BigDecimal minPrice     = vehicleRepository.findMinPrice();
        BigDecimal maxPrice     = vehicleRepository.findMaxPrice();
        Integer minMileage      = vehicleRepository.findMinMileage();
        Integer maxMileage      = vehicleRepository.findMaxMileage();

        return VehicleSearchMetaDto.builder()
                .makes(makes)
                .bodyStyles(bodyStyles)
                .years(years)
                .minPrice(minPrice != null ? minPrice : BigDecimal.ZERO)
                .maxPrice(maxPrice != null ? maxPrice
                        : new BigDecimal("500000"))
                .minMileage(minMileage != null ? minMileage : 0)
                .maxMileage(maxMileage != null ? maxMileage : 200000)
                .build();
    }

    // ── Staff/Admin: create ───────────────────────────────────────────────
    @Transactional
    public VehicleDto create(CreateVehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .make(request.getMake())
                .model(request.getModel())
                .trim(request.getTrim())
                .year(request.getYear())
                .vin(request.getVin())
                .stockNumber(request.getStockNumber())
                .price(request.getPrice())
                .msrp(request.getMsrp())
                .status(request.getStatus() != null
                        ? request.getStatus() : VehicleStatus.AVAILABLE)
                .condition(request.getCondition())
                .bodyStyle(request.getBodyStyle())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .badge(request.getBadge())
                .mileage(request.getMileage())
                .exteriorColor(request.getExteriorColor())
                .interiorColor(request.getInteriorColor())
                .engine(request.getEngine())
                .drivetrain(request.getDrivetrain())
                .doors(request.getDoors())
                .seats(request.getSeats())
                .location(request.getLocation())
                .description(request.getDescription())
                .images(request.getImages() != null
                        ? request.getImages() : new ArrayList<>())
                .primaryImageUrl(request.getPrimaryImageUrl())
                .featured(request.isFeatured())
                .features(request.getFeatures() != null
                        ? request.getFeatures() : new ArrayList<>())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("✅ Vehicle created: {} [{}]",
                saved.getTitle(), saved.getId());
        return vehicleMapper.toDto(saved);
    }

    // ── Staff/Admin: update ───────────────────────────────────────────────
    @Transactional
    public VehicleDto update(UUID id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", id));

        if (request.getImages() != null) {
            List<String> oldImages = vehicle.getImages() != null
                    ? new ArrayList<>(vehicle.getImages())
                    : new ArrayList<>();
            List<String> newImages = request.getImages();
            oldImages.stream()
                    .filter(url -> !newImages.contains(url))
                    .map(cloudinaryService::extractPublicId)
                    .forEach(cloudinaryService::deleteImage);
            vehicle.setImages(newImages);
        }

        if (request.getPrimaryImageUrl() != null
                && vehicle.getPrimaryImageUrl() != null
                && !request.getPrimaryImageUrl().equals(
                        vehicle.getPrimaryImageUrl())) {
            List<String> newImages = request.getImages() != null
                    ? request.getImages() : vehicle.getImages();
            if (newImages == null
                    || !newImages.contains(vehicle.getPrimaryImageUrl())) {
                cloudinaryService.deleteImage(
                        cloudinaryService.extractPublicId(
                                vehicle.getPrimaryImageUrl()));
            }
            vehicle.setPrimaryImageUrl(request.getPrimaryImageUrl());
        } else if (request.getPrimaryImageUrl() != null) {
            vehicle.setPrimaryImageUrl(request.getPrimaryImageUrl());
        }

        if (request.getMake()          != null)
            vehicle.setMake(request.getMake());
        if (request.getModel()         != null)
            vehicle.setModel(request.getModel());
        if (request.getTrim()          != null)
            vehicle.setTrim(request.getTrim());
        if (request.getYear()          != null)
            vehicle.setYear(request.getYear());
        if (request.getVin()           != null)
            vehicle.setVin(request.getVin());
        if (request.getStockNumber()   != null)
            vehicle.setStockNumber(request.getStockNumber());
        if (request.getPrice()         != null)
            vehicle.setPrice(request.getPrice());
        if (request.getMsrp()          != null)
            vehicle.setMsrp(request.getMsrp());
        if (request.getStatus()        != null)
            vehicle.setStatus(request.getStatus());
        if (request.getCondition()     != null)
            vehicle.setCondition(request.getCondition());
        if (request.getBodyStyle()     != null)
            vehicle.setBodyStyle(request.getBodyStyle());
        if (request.getFuelType()      != null)
            vehicle.setFuelType(request.getFuelType());
        if (request.getTransmission()  != null)
            vehicle.setTransmission(request.getTransmission());
        if (request.getBadge()         != null)
            vehicle.setBadge(request.getBadge());
        if (request.getMileage()       != null)
            vehicle.setMileage(request.getMileage());
        if (request.getExteriorColor() != null)
            vehicle.setExteriorColor(request.getExteriorColor());
        if (request.getInteriorColor() != null)
            vehicle.setInteriorColor(request.getInteriorColor());
        if (request.getEngine()        != null)
            vehicle.setEngine(request.getEngine());
        if (request.getDrivetrain()    != null)
            vehicle.setDrivetrain(request.getDrivetrain());
        if (request.getDoors()         != null)
            vehicle.setDoors(request.getDoors());
        if (request.getSeats()         != null)
            vehicle.setSeats(request.getSeats());
        if (request.getLocation()      != null)
            vehicle.setLocation(request.getLocation());
        if (request.getDescription()   != null)
            vehicle.setDescription(request.getDescription());
        if (request.getFeatured()      != null)
            vehicle.setFeatured(request.getFeatured());
        if (request.getFeatures()      != null)
            vehicle.setFeatures(request.getFeatures());

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("✅ Vehicle updated: {} [{}]",
                saved.getTitle(), saved.getId());
        return vehicleMapper.toDto(saved);
    }

    // ── Staff/Admin: patch status ─────────────────────────────────────────
    @Transactional
    public VehicleDto updateStatus(UUID id, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", id));
        vehicle.setStatus(status);
        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("✅ Vehicle status updated: {} → {}",
                saved.getTitle(), status);
        return vehicleMapper.toDto(saved);
    }

    // ── Staff/Admin: delete ───────────────────────────────────────────────
    @Transactional
    public void delete(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", id));

        List<String> allImages = new ArrayList<>();
        if (vehicle.getImages() != null) {
            allImages.addAll(vehicle.getImages());
        }
        if (vehicle.getPrimaryImageUrl() != null
                && !allImages.contains(vehicle.getPrimaryImageUrl())) {
            allImages.add(vehicle.getPrimaryImageUrl());
        }

        if (!allImages.isEmpty()) {
            log.info("🗑️ Deleting {} image(s) for vehicle: {}",
                    allImages.size(), id);
            allImages.stream()
                    .map(cloudinaryService::extractPublicId)
                    .forEach(cloudinaryService::deleteImage);
        }

        vehicleRepository.delete(vehicle);
        log.info("✅ Vehicle deleted: {}", id);
    }
}